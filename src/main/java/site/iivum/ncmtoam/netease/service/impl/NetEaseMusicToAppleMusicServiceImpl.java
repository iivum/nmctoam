package site.iivum.ncmtoam.netease.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import site.iivum.ncmtoam.apple.model.PlaylistItem;
import site.iivum.ncmtoam.apple.model.Track;
import site.iivum.ncmtoam.apple.service.AppleMusicService;
import site.iivum.ncmtoam.netease.api.NCMAPI;
import site.iivum.ncmtoam.netease.handler.ResultHandler;
import site.iivum.ncmtoam.netease.model.Artist;
import site.iivum.ncmtoam.netease.model.Playlist;
import site.iivum.ncmtoam.netease.model.Result;
import site.iivum.ncmtoam.netease.model.Song;
import site.iivum.ncmtoam.netease.service.NetEaseMusicToAppleMusicService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetEaseMusicToAppleMusicServiceImpl implements NetEaseMusicToAppleMusicService {
    public static final String ID_PARAM_NAME = "id";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String CHINESE_LEFT_BRACKET = "（";
    public static final String CHINESE_RIGHT_BRACKET = "）";
    public static final String LEFT_SQUARE_BRACKET = "[";
    public static final String RIGHT_SQUARE_BRACKET = "]";
    private static final String TRACK_TYPES = "songs";
    private static final ExecutorService requestThreadPool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() >> 1);
    private final AppleMusicService appleMusicService;

    @Override
    public List<Song> getNetEaseSongInfosByPlaylistId(long id, Integer limit) throws Exception {
        Playlist playlist = ResultHandler.handle(NCMAPI.playlist(id)).getPlaylist();
        Long[] songIds = playlist.getSongIds().stream().map(Integer::longValue).toArray(Long[]::new);
        if (limit != null) {
            return ResultHandler.handle(NCMAPI.detail(songIds)).getSongs().subList(0, limit);
        }
        return Flux.fromArray(songIds)
                .buffer(1000)
                .map(it -> NCMAPI.detail(it.toArray(new Long[0])))
                .map(ResultHandler::handle)
                .map(Result::getSongs)
                .flatMap(Flux::fromIterable)
                .collect(Collectors.toList())
                .block();


    }

    @Override
    public site.iivum.ncmtoam.apple.model.Song match(Song neteaseSong,
                                                     List<site.iivum.ncmtoam.apple.model.Song> appleMusicSong) {

        if (!CollectionUtils.isEmpty(appleMusicSong)) {

            final Supplier<site.iivum.ncmtoam.apple.model.Song> thirdLevel = () -> appleMusicSong.stream()
                    .min(Comparator.comparingInt(trackers -> totalLevenshteinDistance(neteaseSong, trackers)))
                    .orElse(null);
            final Supplier<site.iivum.ncmtoam.apple.model.Song> secondLevel = () -> appleMusicSong.stream()
                    .filter(it -> it.anyMatch(neteaseSong))
                    .findFirst()
                    .orElseGet(thirdLevel);
            return appleMusicSong.stream()
                    .filter(it -> it.allMatch(neteaseSong))
                    .findFirst()
                    .orElseGet(secondLevel);
        }
        return null;
    }

    @Override
    public site.iivum.ncmtoam.apple.model.Song strictMatch(Song neteaseSong,
                                                           List<site.iivum.ncmtoam.apple.model.Song> appleMusicSong) {
        if (CollectionUtils.isEmpty(appleMusicSong)) {
            return null;
        }
        String neteaseAlbumName = neteaseSong.getAlbumName();
        String neteaseSongName = neteaseSong.getName();
        List<String> neteaseArtists = neteaseSong.getAr().stream().map(Artist::getName).collect(Collectors.toList());

        final Comparator<site.iivum.ncmtoam.apple.model.Song> comparator = Comparator.<site.iivum.ncmtoam.apple.model.Song, Boolean>comparing(song -> neteaseSongName.equalsIgnoreCase(song.getName()))
                .thenComparing(song -> neteaseArtists.stream()
                        .filter(Objects::nonNull).filter(artistName -> artistName.equalsIgnoreCase(song.getArtistName())).count())
                .thenComparing(song -> neteaseAlbumName != null && song.getAlbumName() != null && neteaseAlbumName.equalsIgnoreCase(song.getAlbumName()));
        return appleMusicSong.stream()
                .filter(Objects::nonNull)
                .max(comparator)
                .orElse(null);
    }


    private int totalLevenshteinDistance(Song neteaseSong, site.iivum.ncmtoam.apple.model.Song appleMusicSong) {
        final LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
        final String nSongName = neteaseSong.getName();
        final String aSongName = appleMusicSong.getName();
        final String nAlbumName = neteaseSong.getAlbumName();
        final String aAlbumName = appleMusicSong.getAlbumName();
        final String aArtiestName = appleMusicSong.getArtistName().toLowerCase().trim();
        final int arSum = neteaseSong.getAr()
                .stream()
                .map(Artist::getName)
                .mapToInt(an -> levenshteinDistance.apply(aArtiestName, an))
                .sum();
        return levenshteinDistance.apply(aSongName.toLowerCase().trim(), nSongName.toLowerCase().trim()) * 50 +
                levenshteinDistance.apply(aAlbumName.toLowerCase().trim(), nAlbumName.toLowerCase().trim()) * 30 +
                arSum * 40;
    }

    @Override
    public site.iivum.ncmtoam.apple.model.Playlist genPlaylist(long id, String name, Integer limit, boolean strict) throws Exception {
        List<Song> songs = getNetEaseSongInfosByPlaylistId(id, limit);
        List<PlaylistItem> playlistItemList = songs.stream()
                .map(song ->
                        CompletableFuture.supplyAsync(() -> normalizedSong(song, strict), requestThreadPool))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());


        return site.iivum.ncmtoam.apple.model.Playlist
                .builder()
                .name(name)
                .items(playlistItemList
                        .stream()
                        .filter(playlistItem -> !playlistItem.isFailed())
                        .collect(Collectors.toList()))
                .songs(playlistItemList
                        .stream()
                        .filter(playlistItem -> !playlistItem.isFailed())
                        .map(PlaylistItem::getITunesProduct)
                        .collect(Collectors.toList()))
                .failed(playlistItemList
                        .stream()
                        .filter(PlaylistItem::isFailed)
                        .map(PlaylistItem::getSongName)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String syncPlaylist(long id, String playlistId, String token, boolean strict) throws Exception {
        final site.iivum.ncmtoam.apple.model.Playlist playlist = genPlaylist(id, "anyName", null, strict);
        log.info("play list\n {} ", playlist.getItems().stream().map(PlaylistItem::toString).collect(Collectors.joining("\n")));
        final List<Track> tracks = playlist.getItems()
                .stream()
                .map(item -> Track.builder().id(item.id).types(TRACK_TYPES).build())
                .collect(Collectors.toList());
        Flux.fromIterable(tracks)
                .buffer(50)
                .subscribeOn(Schedulers.single())
                .map(buf -> {
                    final HashMap<String, List<Track>> body = new HashMap<>();
                    body.put("data", buf);
                    return body;
                })
                .doOnEach(body -> {
                    if (body.hasValue()) {
                        appleMusicService.addTracks(body.get(), playlistId, token);
                    }
                })
                .retry()
                .subscribe();
        return String.join("\n", playlist.getFailed());
    }

    private PlaylistItem normalizedSong(Song song, boolean strictMatch) {
        String songName = removeBrackets(song.getName(), LEFT_BRACKET, RIGHT_BRACKET);
        songName = removeBrackets(songName, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET);
        songName = removeBrackets(songName, CHINESE_LEFT_BRACKET, CHINESE_RIGHT_BRACKET).trim();

        String artistsName = song.getAr().stream().map(Artist::getName).collect(Collectors.joining(" "));
        String albumName = song.getAlbumName();
        try {

            songName = URLEncoder.encode(songName, "utf-8");
            artistsName = URLEncoder.encode(artistsName, "utf-8");
            albumName = albumName == null ? "" : URLEncoder.encode(albumName, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String term = songName.trim() + " " +
                artistsName;
        term = term.replaceAll(" ", "+");
        List<site.iivum.ncmtoam.apple.model.Song> appleMusicSongs =
                appleMusicService.search(term);
        if (!appleMusicSongs.isEmpty()) {
            site.iivum.ncmtoam.apple.model.Song matchedSong = strictMatch ?
                    strictMatch(song, appleMusicSongs)
                    : match(song, appleMusicSongs);
            if (matchedSong != null) {
                return new PlaylistItem(matchedSong);
            }
        }

        return new PlaylistItem(-1, song.getName(), true);
    }

    private String removeBrackets(String s, String left, String right) {
        s = Objects.requireNonNull(s);
        if (s.contains(left) && s.contains(right)) {
            return removeBrackets(s.substring(0, s.indexOf(left)) +
                    s.substring(s.lastIndexOf(right) + 1).trim(), left, right);
        }
        return s;
    }


    private long resolveUrl(String url) {
        String paramsString = url.substring(url.indexOf("?") + 1);
        String[] params = paramsString.split("&");
        for (String param : params) {
            if (param.startsWith(ID_PARAM_NAME)) {
                return Long.parseLong(param.substring(param.indexOf(ID_PARAM_NAME) + 3));
            }
        }
        throw new RuntimeException("错误的url");
    }

}

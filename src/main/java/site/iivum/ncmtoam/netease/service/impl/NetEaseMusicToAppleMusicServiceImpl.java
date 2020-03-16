package site.iivum.ncmtoam.netease.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import site.iivum.ncmtoam.apple.model.PlaylistItem;
import site.iivum.ncmtoam.apple.service.AppleMusicService;
import site.iivum.ncmtoam.netease.api.NCMAPI;
import site.iivum.ncmtoam.netease.handler.ResultHandler;
import site.iivum.ncmtoam.netease.model.Artist;
import site.iivum.ncmtoam.netease.model.Playlist;
import site.iivum.ncmtoam.netease.model.Song;
import site.iivum.ncmtoam.netease.service.NetEaseMusicToAppleMusicService;

import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
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
    private static final ForkJoinPool requestThreadPool = new ForkJoinPool(20);
    private final AppleMusicService appleMusicService;

    @Override
    public List<Song> getNetEaseSongInfosByPlaylistId(long id, Integer limit) throws Exception {
        Playlist playlist = ResultHandler.handle(NCMAPI.playlist(id)).getPlaylist();
        long[] songIds = playlist.getSongIds().stream().mapToLong(i -> (long) i).toArray();
        if (limit != null) {
            return ResultHandler.handle(NCMAPI.detail(songIds)).getSongs().subList(0, limit);
        }
        return ResultHandler.handle(NCMAPI.detail(songIds)).getSongs();
    }

    @Override
    public site.iivum.ncmtoam.apple.model.Song match(Song neteaseSong,
                                                     List<site.iivum.ncmtoam.apple.model.Song> appleMusicSong) {
        if (!CollectionUtils.isEmpty(appleMusicSong)) {
            String neteaseAlbumName = neteaseSong.getAlbumName();
            String neteaseSongName = neteaseSong.getName();
            List<String> neteaseArtists = neteaseSong.getAr().stream().map(Artist::getName).collect(Collectors.toList());
            for (site.iivum.ncmtoam.apple.model.Song song : appleMusicSong) {
                if (neteaseAlbumName.equalsIgnoreCase(song.getAlbumName()) ||
                        neteaseSongName.equalsIgnoreCase(song.getName()) ||
                        neteaseArtists.stream().anyMatch(artistName -> artistName.equalsIgnoreCase(song.getArtistName()))) {
                    return song;
                }
            }
        }
        return null;
    }

    @Override
    public site.iivum.ncmtoam.apple.model.Playlist genPlaylist(long id, String name, Integer limit) throws Exception {
//        long id = resolveUrl(url);
        List<Song> songs = getNetEaseSongInfosByPlaylistId(id, limit);
        List<PlaylistItem> playlistItemList = songs.stream()
                .map(song ->
                        CompletableFuture.supplyAsync(() -> normalizedSong(song), requestThreadPool))
                .collect(Collectors.toList()).stream().map(CompletableFuture::join)
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

    @SneakyThrows
    private PlaylistItem normalizedSong(Song song) {
        String songName = removeBrackets(song.getName(), LEFT_BRACKET, RIGHT_BRACKET);
        songName = removeBrackets(songName, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET);
        songName = removeBrackets(songName, CHINESE_LEFT_BRACKET, CHINESE_RIGHT_BRACKET);
        String encodedName = URLEncoder.encode(songName.trim(), "utf-8");
        List<site.iivum.ncmtoam.apple.model.Song> appleMusicSongs =
                appleMusicService.search(encodedName);
        if (!appleMusicSongs.isEmpty()) {
            site.iivum.ncmtoam.apple.model.Song matchedSong = match(song, appleMusicSongs);
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
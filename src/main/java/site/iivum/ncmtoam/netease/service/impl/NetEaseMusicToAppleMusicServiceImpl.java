package site.iivum.ncmtoam.netease.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import site.iivum.ncmtoam.apple.AppleMusicApi;
import site.iivum.ncmtoam.apple.model.ResponseRoot;
import site.iivum.ncmtoam.apple.model.Result;
import site.iivum.ncmtoam.netease.api.NCMAPI;
import site.iivum.ncmtoam.netease.handler.ResultHandler;
import site.iivum.ncmtoam.netease.model.Artist;
import site.iivum.ncmtoam.netease.model.Playlist;
import site.iivum.ncmtoam.netease.model.Song;
import site.iivum.ncmtoam.netease.service.NetEaseMusicToAppleMusicService;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetEaseMusicToAppleMusicServiceImpl implements NetEaseMusicToAppleMusicService {
    public static final String SONG_TYPES = "songs";
    public static final String ID_PARAM_NAME = "id";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String CHINESE_LEFT_BRACKET = "（";
    public static final String CHINESE_RIGHT_BRACKET = "）";
    public static final String LEFT_SQUARE_BRACKET = "[";
    public static final String RIGHT_SQUARE_BRACKET = "]";
    private final AppleMusicApi appleMusicApi;

    @Override
    public List<Song> getNetEaseSongInfosByPlaylistId(long id) throws Exception {
        Playlist playlist = ResultHandler.handle(NCMAPI.playlist(id)).getPlaylist();
        long[] songIds = playlist.getSongIds().stream().mapToLong(i -> (long) i).toArray();
        return ResultHandler.handle(NCMAPI.detail(songIds)).getSongs();
    }

    @Override
    public List<site.iivum.ncmtoam.apple.model.Song> getAppleMusicSongs(String name) {
        ResponseEntity<ResponseRoot> searchResult = appleMusicApi.search(name, SONG_TYPES);
        if (!searchResult.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("无法请求apple music");
        }
        return Optional.ofNullable(searchResult.getBody())
                .map(ResponseRoot::getResults).map(Result::getSongs).orElse(Collections.emptyList());
    }

    @Override
    public site.iivum.ncmtoam.apple.model.Song match(Song neteaseSong,
                                                     List<site.iivum.ncmtoam.apple.model.Song> appleMusicSong) {
        if (CollectionUtils.isEmpty(appleMusicSong)) {
            return null;
        }
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
        return null;
    }

    @Override
    public site.iivum.ncmtoam.apple.model.Playlist genPlaylist(long id, String name) throws Exception {
//        long id = resolveUrl(url);
        List<Song> songs = getNetEaseSongInfosByPlaylistId(id);
        List<PlaylistItem> playlistItemList = songs.stream()
                .map(song -> CompletableFuture.supplyAsync(() -> {
                    String normalizedSongName = normalizedSongName(song);
                    if (normalizedSongName == null) {
                        return new PlaylistItem(song.getName(), true);
                    }
                    return new PlaylistItem(normalizedSongName, false);
                })).collect(Collectors.toList()).stream().map(CompletableFuture::join)
                .collect(Collectors.toList());


        return site.iivum.ncmtoam.apple.model.Playlist
                .builder()
                .name(name)
                .songs(playlistItemList
                        .stream()
                        .filter(playlistItem -> !playlistItem.failed)
                        .map(PlaylistItem::getSongName)
                        .collect(Collectors.toList()))
                .failed(playlistItemList
                        .stream()
                        .filter(PlaylistItem::isFailed)
                        .map(PlaylistItem::getSongName)
                        .collect(Collectors.toList()))
                .build();
    }

    @SneakyThrows
    private String normalizedSongName(Song song) {
        String songName = removeBrackets(song.getName(), LEFT_BRACKET, RIGHT_BRACKET);
        songName = removeBrackets(songName, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET);
        songName = removeBrackets(songName, CHINESE_LEFT_BRACKET, CHINESE_RIGHT_BRACKET);
        String encodedName = URLEncoder.encode(songName.trim(), "utf-8");
        List<site.iivum.ncmtoam.apple.model.Song> appleMusicSongs =
                getAppleMusicSongs(encodedName);
        if (appleMusicSongs.isEmpty()) {
            return null;
        }
        return Optional.ofNullable(match(song, appleMusicSongs)).map(site.iivum.ncmtoam.apple.model.Song::getName)
                .orElse(null);
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

    @Data
    private static class PlaylistItem {
        String songName;
        boolean failed;

        public PlaylistItem(String songName, boolean failed) {
            this.songName = songName;
            this.failed = failed;
        }
    }


}

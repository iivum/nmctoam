package site.iivum.ncmtoam.netease.service;

import site.iivum.ncmtoam.apple.model.Playlist;
import site.iivum.ncmtoam.netease.model.Song;

import java.util.List;

public interface NetEaseMusicToAppleMusicService {
    List<Song> getNetEaseSongInfosByPlaylistId(long id, Integer limit) throws Exception;

    site.iivum.ncmtoam.apple.model.Song match(Song neteaseSong,
                                              List<site.iivum.ncmtoam.apple.model.Song> appleMusicSong);

    Playlist genPlaylist(long id, String name, Integer limit) throws Exception;

    String syncPlaylist(long id, String playlistId, String token) throws Exception;
}

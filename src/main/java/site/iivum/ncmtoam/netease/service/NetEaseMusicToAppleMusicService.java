package site.iivum.ncmtoam.netease.service;

import site.iivum.ncmtoam.apple.model.Playlist;
import site.iivum.ncmtoam.netease.model.Song;

import java.util.List;

public interface NetEaseMusicToAppleMusicService {
    List<Song> getNetEaseSongInfosByPlaylistId(long id) throws Exception;

    List<site.iivum.ncmtoam.apple.model.Song> getAppleMusicSongs(String name);

    site.iivum.ncmtoam.apple.model.Song match(Song neteaseSong,
                                              List<site.iivum.ncmtoam.apple.model.Song> appleMusicSong);

    Playlist genPlaylist(long id, String name) throws Exception;
}

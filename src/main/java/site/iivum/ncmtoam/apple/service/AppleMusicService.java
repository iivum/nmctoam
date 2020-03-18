package site.iivum.ncmtoam.apple.service;

import site.iivum.ncmtoam.apple.model.Song;
import site.iivum.ncmtoam.apple.model.Track;

import java.util.List;
import java.util.Map;

public interface AppleMusicService {
    List<Song> search(String term);

    public void addTracks(Map<String, List<Track>> tracks,
                          String id,
                          String token);
}

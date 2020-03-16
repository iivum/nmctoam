package site.iivum.ncmtoam.apple.service;

import site.iivum.ncmtoam.apple.model.Song;

import java.util.List;

public interface AppleMusicService {
    List<Song> search(String term);
}

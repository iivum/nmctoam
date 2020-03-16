package site.iivum.ncmtoam.apple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import site.iivum.ncmtoam.apple.AppleMusicApi;
import site.iivum.ncmtoam.apple.model.ResponseRoot;
import site.iivum.ncmtoam.apple.model.Result;
import site.iivum.ncmtoam.apple.model.Song;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppleMusicServiceImpl implements AppleMusicService {
    public static final String SONG_TYPES = "songs";

    private final AppleMusicApi appleMusicApi;

    @Override
    @Cacheable(cacheNames = "appleMusic", key = "#term")
    public List<Song> search(String term) {
        ResponseEntity<ResponseRoot> searchResult = appleMusicApi.search(term, SONG_TYPES);
        if (!searchResult.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("无法请求apple music");
        }
        return Optional.ofNullable(searchResult.getBody())
                .map(ResponseRoot::getResults).map(Result::getSongs).orElse(Collections.emptyList());
    }
}

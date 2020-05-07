package site.iivum.ncmtoam.apple.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import site.iivum.ncmtoam.apple.AppleMusicApi;
import site.iivum.ncmtoam.apple.model.ResponseRoot;
import site.iivum.ncmtoam.apple.model.Result;
import site.iivum.ncmtoam.apple.model.Song;
import site.iivum.ncmtoam.apple.model.Track;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppleMusicServiceImpl implements AppleMusicService {
    public static final String SONG_TYPES = "songs";
    public static final int DEFAULT_LIMIT = 20;
    private final AppleMusicApi appleMusicApi;

    @Override
    @Cacheable(cacheNames = "appleMusic", key = "#term")
    public List<Song> search(String term) {
        ResponseEntity<ResponseRoot> searchResult = appleMusicApi.search(term, SONG_TYPES, DEFAULT_LIMIT);
        if (!searchResult.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("无法请求apple music");
        }
        return Optional.ofNullable(searchResult.getBody())
                .map(ResponseRoot::getResults).map(Result::getSongs).orElse(Collections.emptyList());
    }

    @Override
    public void addTracks(Map<String, List<Track>> tracks,
                          String id,
                          String token) {
        try {
            appleMusicApi.addTracksToPlaylist(tracks, id, token);
        } catch (FeignException.GatewayTimeout e) {
            log.warn("", e);
        }
    }
}

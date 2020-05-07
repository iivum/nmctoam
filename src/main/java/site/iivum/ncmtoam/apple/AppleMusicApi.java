package site.iivum.ncmtoam.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.iivum.ncmtoam.apple.model.ResponseRoot;
import site.iivum.ncmtoam.apple.model.Track;

import java.util.List;
import java.util.Map;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@FeignClient(name = "appleMusicApi", url = "https://api.music.apple.com", decode404 = true)
@RestController
@RequestMapping(value = "/v1",
        headers = "Authorization=Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IldlYlBsYXlLaWQifQ.eyJpYXQiOjE1ODc" +
                "0OTYxNDksImV4cCI6MTU5MzcxNjk0OSwiaXNzIjoiQU1QV2ViUGxheSJ9.Hqatp-1xg8JMDxopNMmHPw_KFloBN9YWqyev4gba9ayi" +
                "k5uBP6FQeiqH-YiIJk4JUKlmuetbMVZSnNadJ1w3VA")
public interface AppleMusicApi {
    @GetMapping(path = "/catalog/cn/search")
    ResponseEntity<ResponseRoot> search(@RequestParam("term") String term,
                                        @RequestParam("types") String types,
                                        @RequestParam("limit") int limit);

    @PostMapping(path = "/me/library/playlists/{id}/tracks")
    ResponseEntity<?> addTracksToPlaylist(@RequestBody Map<String, List<Track>> tracks,
                                          @PathVariable("id") String id,
                                          @RequestHeader("music-user-token") String token);
}

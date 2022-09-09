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
        headers = "Authorization=Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlEzOE1TVTZEWUwifQ.eyJpYXQiOjE2NDg5MDczOTUsImV4cCI6MTY2NDQ1OTM5NSwiaXNzIjoiQkxISDVRN0xZWiJ9.ADpAb2cAeAGvQ3oHCzPrp-8Vz-Na2NIlrXttAm7BcfLxpPLD27gSO8dtQkjnS8miyrS0-O5ghYbGd8aPDR4YFA")
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

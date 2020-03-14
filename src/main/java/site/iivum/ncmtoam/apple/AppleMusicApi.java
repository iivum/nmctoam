package site.iivum.ncmtoam.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.iivum.ncmtoam.apple.model.ResponseRoot;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@FeignClient(name = "appleMusicApi", url = "https://api.music.apple.com", decode404 = true)
@RestController
@RequestMapping(value = "/v1",
        headers = "Authorization=Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ild" +
                "ZNUc4Nk4yMjIifQ.eyJpYXQiOjE1NzI5MDIzNjQsImV4cCI6MTU4ODYyNzE2NCwiaXNzIj" +
                "oiSloyVE1BR0syMyJ9.hryC3bdoqNOm_Yo9j8lQjnBxYYLUCuBY3cRsIyoPmks4DTv2Iwv" +
                "yGzAc8V8AJ3LtAz2G6m_bCu8JgxbvgJbTvg")
public interface AppleMusicApi {
    @GetMapping(path = "/catalog/cn/search")
    ResponseEntity<ResponseRoot> search(@RequestParam("term") String term, @RequestParam("types") String types);
}

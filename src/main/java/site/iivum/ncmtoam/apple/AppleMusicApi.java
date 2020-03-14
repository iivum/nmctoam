package site.iivum.ncmtoam.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@FeignClient(name = "appleMusicApi", url = "https://api.music.apple.com", path = "/v1")
@RestController
public class AppleMusicApi {
    public
}

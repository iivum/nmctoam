package site.iivum.ncmtoam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.iivum.ncmtoam.apple.model.Playlist;
import site.iivum.ncmtoam.netease.service.NetEaseMusicToAppleMusicService;

@RestController
@RequiredArgsConstructor
public class NetEaseMusicToAppleMusicController {
    private final NetEaseMusicToAppleMusicService netEaseMusicToAppleMusicService;

    @GetMapping("/genPlaylist")
    public Playlist genPlaylist(@RequestParam("id") long id, @RequestParam(required = false, name = "name") String name) throws Exception {
        if (name == null) {
            name = "我喜欢的音乐";
        }
        return netEaseMusicToAppleMusicService.genPlaylist(id, name);
    }
}

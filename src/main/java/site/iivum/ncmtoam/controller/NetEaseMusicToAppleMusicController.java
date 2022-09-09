package site.iivum.ncmtoam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import site.iivum.ncmtoam.apple.model.Playlist;
import site.iivum.ncmtoam.netease.service.NetEaseMusicToAppleMusicService;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.Executors;


@Slf4j
@RestController
@RequiredArgsConstructor
public class NetEaseMusicToAppleMusicController {
    private final NetEaseMusicToAppleMusicService netEaseMusicToAppleMusicService;

    @GetMapping("/genPlaylist")
    public Playlist genPlaylist(@RequestParam("id") long id,
                                @RequestParam(required = false, name = "name") String name,
                                @RequestParam(name = "limit", required = false) Integer limit,
                                @RequestParam(defaultValue = "false") boolean strict) throws Exception {
        if (name == null) {
            name = "我喜欢的音乐";
        }
        return netEaseMusicToAppleMusicService.genPlaylist(id, name, limit, strict);
    }

    @GetMapping("/syncPlaylist")
    public DeferredResult<String> syncPlaylist(@RequestParam("id") long id,
                                               @RequestParam("playlistId") String playlistId,
                                               @RequestHeader("music-user-token") String token,
                                               @RequestParam(defaultValue = "true") boolean strict) throws Exception {
        final DeferredResult<String> result = new DeferredResult<>(Duration.ofMinutes(30).toMillis());

        Executors.newSingleThreadExecutor()
                .submit(() -> {
                    try {
                        result.setResult(netEaseMusicToAppleMusicService.syncPlaylist(id, playlistId, token, strict));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> whenServerFails(Exception ex, HttpServletRequest request) {
        log.error("服务端错误 [method={}\turl={}\tquery={}]",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                ex);


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

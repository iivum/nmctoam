package site.iivum.ncmtoam.netease.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 2020/3/16
 *
 * @author lbh
 */
@Slf4j
@SpringBootTest
class NetEaseMusicToAppleMusicServiceTest {
    @Autowired
    NetEaseMusicToAppleMusicService service;

    @org.junit.jupiter.api.Test
    void syncPlaylist() throws Exception {
        log.info(service.syncPlaylist(466675576, "p.eoGxxMoTQ3JKbN",
                "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IldlYlBsYXlLaWQifQ.eyJpYXQiOjE2NDM0MDg4NzgsImV4cCI6MTY0OTYyOTY3OCwiaXNzIjoiQU1QV2ViUGxheSJ9.lhOg-whQXp_7UB9Q8EVyRqmR2sW6DcAHDYKGo9aEY4tII_2WD7t9Pa96QMUWqy1cqsehDDjQT65L6xm3DO7orQ", true));
    }


}
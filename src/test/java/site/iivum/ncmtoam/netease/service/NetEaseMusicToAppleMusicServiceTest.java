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
                "Ah1Dy1O+q6/1/YoWV6o3CJrAfOlPzKtEQhY+fN7QwwPtRw952eb2SN2W5H3mlHMIYRHJ6JYrkIvBiq9Z1cADolzUuHjzjQe/mffN" +
                        "yxrpvO2CoJMlSMHqNFbB8JMyuv2RPAJx+Ekh3XLazLspMsvIoY9YOEoiPRj1VSrtTdA+VHKfmzxE/iOqKc98eZesHkMh" +
                        "9I/fDcoRhx0Jon94z+KoRcVxZ1XuQBUY++f8tGndFjr6/8Pssg=="));
    }
}
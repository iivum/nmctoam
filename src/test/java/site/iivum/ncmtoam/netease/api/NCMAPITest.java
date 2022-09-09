package site.iivum.ncmtoam.netease.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * description here
 *
 * @author lbh
 * @since ncmtoam v
 */
@Slf4j
class NCMAPITest {

    @Test
    void artist() {
    }

    @Test
    void album() {
    }

    @Test
    void playlist() throws Exception {
        log.info(NCMAPI.playlist(466675576));
    }

    @Test
    void testPlaylist() {
    }

    @Test
    void lyric() {
    }

    @Test
    void mv() {
    }
}
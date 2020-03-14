package site.iivum.ncmtoam.netease;

import org.junit.jupiter.api.Test;
import site.iivum.ncmtoam.netease.api.NCMAPI;
import site.iivum.ncmtoam.netease.handler.ResultHandler;
import site.iivum.ncmtoam.netease.model.Result;

/**
 * 2020/3/8
 *
 * @author lbh
 */
class NCMAPITest {

    @Test
    void search() {

    }

    @Test
    void testSearch() {
    }

    @Test
    void testSearch1() {
    }

    @Test
    void testSearch2() {
    }

    @Test
    void artist() throws Exception {
        System.err.println(NCMAPI.artist(1050518L));
    }

    @Test
    void album() {
    }

    @Test
    void detail() throws Exception {
        System.err.println(NCMAPI.detail(1050518));
    }

    @Test
    void playlist() throws Exception {
        final String playlist = NCMAPI.playlist(466675576L);
        final Result playList = ResultHandler.handle(playlist);
        System.err.println(playList);
    }

    @Test
    void testPlaylist() {
    }
}
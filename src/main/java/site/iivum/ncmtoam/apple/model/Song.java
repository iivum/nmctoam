package site.iivum.ncmtoam.apple.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@Builder
@Getter
@Setter
public class Song implements Serializable {
    private long id;
    private String name;
    private String artistName;
    private String albumName;
    private String url;

    @Override
    public String toString() {
        return String.format("%s，艺人：%s (%s&uo=4)", name, artistName, url);
    }
}

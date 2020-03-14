package site.iivum.ncmtoam.apple.model;

import lombok.Builder;
import lombok.Data;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@Builder
@Data
public class Song {
    private String name;
    private String artistName;
    private String albumName;
}

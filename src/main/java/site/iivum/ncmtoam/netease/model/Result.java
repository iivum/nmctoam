package site.iivum.ncmtoam.netease.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Result {
    private int code;
    private Playlist playlist;
    private Artist artist;
    private List<Song> songs;
}

package site.iivum.ncmtoam.netease.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Song {
    private long id;
    private String name;
    private List<Artist> ar;
    private String albumName;

    @JsonProperty("al")
    public void setAlbumName(Map<String, Object> albumDetail) {
        if (!albumDetail.isEmpty()) {
            this.albumName = ((String) albumDetail.get("name"));
        }
    }
}

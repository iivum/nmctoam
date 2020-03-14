package site.iivum.ncmtoam.netease.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {

    private String creator;
    private List<Integer> songIds;
    private String name;

    @JsonProperty("creator")
    public void setCreator(Map<String, Object> creator) {
        this.creator = ((String) Optional.ofNullable(creator)
                .map(c -> c.getOrDefault("nickname", ""))
                .orElse(""));
    }

    @JsonProperty("trackIds")
    public void setSongIds(List<Map<String, Object>> songIds) {
        this.songIds = Optional.ofNullable(songIds)
                .map(trackIds ->
                        trackIds.stream()
                                .map(trackId -> trackId.getOrDefault("id", ""))
                                .map(trackId -> ((Integer) trackId))
                                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}

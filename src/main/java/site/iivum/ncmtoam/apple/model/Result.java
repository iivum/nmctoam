package site.iivum.ncmtoam.apple.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Result {
    List<Song> songs;

    @SuppressWarnings("unchecked")
    @JsonProperty("songs")
    public void setSongs(Map<String, Object> songs) {
        if (!songs.isEmpty()) {
            this.songs = Optional.ofNullable(((List<Map<String, Object>>) songs.get("data")))
                    .map(data -> data.stream()
                            .map(datum -> ((Map<String, Object>) datum.get("attributes")))
                            .map(datum -> Song
                                    .builder().name(((String) datum.get("name")))
                                    .artistName(((String) datum.get("artistName")))
                                    .albumName(((String) datum.get("albumName")))
                                    .build())
                            .collect(Collectors.toList())).get();

        }
    }
}

package site.iivum.ncmtoam.apple.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Playlist {
    String name;
    List<PlaylistItem> items;
    List<String> songs;
    List<String> failed;
}

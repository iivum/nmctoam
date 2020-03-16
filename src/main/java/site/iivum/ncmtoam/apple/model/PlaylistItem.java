package site.iivum.ncmtoam.apple.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistItem {
    public long id;
    public String songName;
    public String iTunesProduct;
    public boolean failed;

    public PlaylistItem(site.iivum.ncmtoam.apple.model.Song song) {
        this.songName = song.getName();
        this.id = song.getId();
        this.failed = false;
        this.iTunesProduct = song.toString();
    }

    public PlaylistItem(long id, String songName, boolean failed) {
        this.id = id;
        this.songName = songName;
        this.failed = failed;
    }
}

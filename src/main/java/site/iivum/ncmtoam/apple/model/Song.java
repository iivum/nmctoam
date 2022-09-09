package site.iivum.ncmtoam.apple.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.iivum.ncmtoam.netease.model.Artist;

import java.io.Serializable;
import java.util.List;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@Builder
@Getter
@Setter
public class Song implements Serializable {

    private static final long serialVersionUID = -5303347934298597948L;

    private long id;
    private String name;
    private String artistName;
    private String albumName;
    private String url;

    @Override
    public String toString() {
        return String.format("%s，艺人：%s (%s&uo=4)", name, artistName, url);
    }

    public boolean anyMatch(site.iivum.ncmtoam.netease.model.Song song) {
        final String albumName = song.getAlbumName();
        final String name = song.getName();
        final List<Artist> ar = song.getAr();
        return this.albumName.equalsIgnoreCase(albumName) || this.name.equalsIgnoreCase(name)
                || ar.stream().map(Artist::getName).anyMatch(it -> it.equals(artistName));
    }

    public boolean allMatch(site.iivum.ncmtoam.netease.model.Song song) {
        final String name = song.getName();
        final List<Artist> ar = song.getAr();
        return this.name.equalsIgnoreCase(name)
                && ar.stream().map(Artist::getName).anyMatch(it -> it.equals(artistName));
    }
}

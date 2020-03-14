package site.iivum.ncmtoam.netease.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist {
    public long id;
    public String name;
}

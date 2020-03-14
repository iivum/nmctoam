package site.iivum.ncmtoam.apple.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 2020/3/8
 *
 * @author lbh
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseRoot {
    private Result results;
}

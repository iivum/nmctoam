package site.iivum.ncmtoam.apple.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("apple-music")
@Data
public class AppleMusicConfig {
    private String developToken;
}

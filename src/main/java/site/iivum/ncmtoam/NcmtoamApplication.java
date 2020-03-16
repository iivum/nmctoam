package site.iivum.ncmtoam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ConfigurationPropertiesScan("site.iivum.ncmtoam.apple.config")
@EnableFeignClients
@EnableCaching
@SpringBootApplication
public class NcmtoamApplication {

    public static void main(String[] args) {
        SpringApplication.run(NcmtoamApplication.class, args);
    }

}

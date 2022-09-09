package site.iivum.ncmtoam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@ConfigurationPropertiesScan("site.iivum.ncmtoam.apple.config")
@EnableFeignClients
@EnableAsync
@EnableCaching
@SpringBootApplication
public class NcmtoamApplication {

    public static void main(String[] args) {
        SpringApplication.run(NcmtoamApplication.class, args);
    }

}

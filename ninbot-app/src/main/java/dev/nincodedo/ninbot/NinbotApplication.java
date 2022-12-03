package dev.nincodedo.ninbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EntityScan("dev.nincodedo")
@EnableJpaRepositories("dev.nincodedo")
@EnableFeignClients
@ComponentScan("dev.nincodedo")
@ConfigurationPropertiesScan("dev.nincodedo")
public class NinbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(NinbotApplication.class, args);
    }

}

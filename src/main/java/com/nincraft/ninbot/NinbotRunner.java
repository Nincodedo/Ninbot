package com.nincraft.ninbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NinbotRunner {
    public static void main(String[] args) {
        SpringApplication.run(Ninbot.class, args);
    }
}

package com.nincraft.ninbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.nincraft.ninbot"})
@SpringBootApplication
public class NinbotRunner {
    public static void main(String[] args) {
        SpringApplication.run(Ninbot.class, args);
    }
}

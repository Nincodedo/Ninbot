package com.nincraft.ninbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NinbotRunner {
    public static void main(String[] args) {
        SpringApplication.run(Ninbot.class, args);
    }
}

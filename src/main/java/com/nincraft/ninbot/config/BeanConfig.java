package com.nincraft.ninbot.config;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.security.auth.login.LoginException;

@Configuration
@ComponentScan(basePackages = {"com.nincraft.ninbot"})
@PropertySource({"classpath:ninbot.properties"})
@Log4j2
public class BeanConfig {

    private final Environment environment;

    @Autowired
    public BeanConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "jda")
    public JDA jda() throws InterruptedException {
        JDA jda = null;
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(environment.getProperty("ninbotToken")).buildBlocking();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
            throw e;
        }
        return jda;
    }
}

package com.nincraft.ninbot.config;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.nincraft.ninbot"})
@PropertySource({"classpath:ninbot.properties"})
@Log4j2
public class BeanConfig {

    @Value("${ninbotToken}")
    private String ninbotToken;

    @Bean
    public List<String> roleBlackList() {
        return new ArrayList<>(Arrays.asList("admin", "mods", "AIRHORN SOLUTIONS", "@everyone", "Dad Bot"));
    }

    @Bean
    public JDA jda() throws InterruptedException {
        JDA jda = null;
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(ninbotToken).buildBlocking();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
            throw e;
        }
        assert jda != null;
        return jda;
    }
}

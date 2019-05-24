package com.nincraft.ninbot.beans;

import com.nincodedo.recast.RecastAPI;
import com.nincodedo.recast.RecastAPIBuilder;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.nincraft.ninbot"})
@Log4j2
public class ApplicationBean {

    @Value("${ninbotToken}")
    private String ninbotToken;

    @Value("${recastToken}")
    private String recastToken;

    @Autowired
    @Bean
    public JDA jda(List<ListenerAdapter> listenerAdapters) throws InterruptedException {
        JDA jda = null;
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(ninbotToken).build();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        }
        assert jda != null;
        jda.awaitReady();
        jda.addEventListener(listenerAdapters.toArray());
        return jda;
    }

    @Bean
    public RecastAPI recastAPI() {
        return new RecastAPIBuilder(recastToken).build();
    }
}

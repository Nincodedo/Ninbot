package com.nincraft.ninbot;

import com.nincraft.ninbot.events.CommandListener;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
@UtilityClass
public class Ninbot {

    public static void main(String[] args) throws InterruptedException, IOException {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("ninbot.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Unable to load properties", e);
            throw e;
        }
        JDA jda = null;
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(properties.getProperty("ninbotToken")).buildBlocking();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
            throw e;
        } catch (RateLimitedException e) {
            log.warn("Rate limit exceeded", e);
        }
        assert jda != null;
        jda.addEventListener(new CommandListener());
    }
}

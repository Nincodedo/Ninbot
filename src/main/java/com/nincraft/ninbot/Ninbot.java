package com.nincraft.ninbot;

import com.nincraft.ninbot.dao.EventDao;
import com.nincraft.ninbot.dao.IEventDao;
import com.nincraft.ninbot.listeners.CommandListener;
import com.nincraft.ninbot.listeners.ReactionListener;
import com.nincraft.ninbot.scheduler.EventScheduler;
import com.nincraft.ninbot.util.Reference;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.flywaydb.core.Flyway;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
@UtilityClass
public class Ninbot {

    private JDA jda;

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties properties = readPropertiesFile();
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
        boolean debugEnabled = Boolean.parseBoolean(properties.getProperty("debugEnabled"));
        IEventDao eventDao = new EventDao();
        EventScheduler eventScheduler = new EventScheduler(jda, eventDao, debugEnabled);
        jda.addEventListener(new CommandListener(eventDao, eventScheduler, debugEnabled));
        jda.addEventListener(new ReactionListener());
        migrateDb();
        eventScheduler.scheduleAll();
        jda.getPresence().setGame(Game.of("say \"@Ninbot help\" for list of commands"));
    }

    private static void migrateDb() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(Reference.SQLITE_DB, null, null);
        flyway.migrate();
    }

    private static Properties readPropertiesFile() throws IOException {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream(Reference.NINBOT_PROPERTIES);
        if (inputStream == null) {
            log.warn("Unable to load properties from classpath, retrying from current directory");
            try {
                inputStream = new FileInputStream(Reference.NINBOT_PROPERTIES);
            } catch (FileNotFoundException e) {
                log.error("Could not find property file", e);
                throw e;
            }
        }
        try {
            properties.load(inputStream);
            log.info("Property file loaded");
        } catch (IOException e) {
            log.error("Unable to load property file", e);
            throw e;
        }
        return properties;
    }
}

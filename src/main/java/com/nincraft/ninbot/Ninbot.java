package com.nincraft.ninbot;

import com.nincraft.ninbot.components.command.CommandListener;
import com.nincraft.ninbot.components.event.EventDao;
import com.nincraft.ninbot.components.event.EventScheduler;
import com.nincraft.ninbot.components.reaction.ReactionListener;
import com.nincraft.ninbot.util.Reference;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.EntityBuilder;
import org.flywaydb.core.Flyway;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
@ComponentScan({"com.nincraft.ninbot"})
@SpringBootApplication
public class Ninbot {

    @Getter
    private static JDA jda;

    public static void main(String[] args) throws IOException, InterruptedException {
        ApplicationContext context = SpringApplication.run(Ninbot.class, args);

        Properties properties = readPropertiesFile();
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(properties.getProperty("ninbotToken")).buildBlocking();
        } catch (LoginException e) {
            log.error("Failed to login", e);
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
            throw e;
        }
        assert jda != null;
        boolean debugEnabled = Boolean.parseBoolean(properties.getProperty("debugEnabled"));

        EventDao eventDao = (EventDao) context.getBean("eventDao");
        EventScheduler eventScheduler = new EventScheduler(jda, eventDao, debugEnabled);
        jda.addEventListener(new CommandListener(eventDao, eventScheduler, debugEnabled));
        jda.addEventListener(new ReactionListener());
        migrateDb();
        eventScheduler.scheduleAll();
        setupPresence();
    }

    private static void setupPresence() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "you");
        jsonObject.put("type", 2);
        val game = EntityBuilder.createGame(jsonObject);
        Ninbot.getJda().getPresence().setGame(game);
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

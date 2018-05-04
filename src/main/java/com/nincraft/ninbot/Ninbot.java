package com.nincraft.ninbot;

import com.nincraft.ninbot.components.command.CommandListener;
import com.nincraft.ninbot.components.event.EventDao;
import com.nincraft.ninbot.components.event.EventScheduler;
import com.nincraft.ninbot.components.reaction.ReactionListener;
import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
@ComponentScan({"com.nincraft.ninbot"})
public class Ninbot {

    @Autowired
    private JDA jda;

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

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            Properties properties = readPropertiesFile();
            assert jda != null;
            boolean debugEnabled = Boolean.parseBoolean(properties.getProperty("debugEnabled"));

            EventDao eventDao = (EventDao) context.getBean("eventDao");
            EventScheduler eventScheduler = (EventScheduler) context.getBean("eventScheduler");
            jda.addEventListener(new CommandListener(eventDao, eventScheduler, debugEnabled));
            jda.addEventListener(new ReactionListener());
            migrateDb();
            eventScheduler.scheduleAll();
            jda.getPresence().setGame(Game.playing("say \"@Ninbot help\" for list of commands"));
        };
    }
}

package com.nincraft.ninbot;

import com.nincraft.ninbot.components.command.CommandListener;
import com.nincraft.ninbot.components.event.EventScheduler;
import com.nincraft.ninbot.components.reaction.ReactionListener;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@Log4j2
@ComponentScan({"com.nincraft.ninbot"})
public class Ninbot {

    @Autowired
    private JDA jda;

    @Autowired
    private CommandListener commandListener;

    @Autowired
    private ReactionListener reactionListener;

    @Autowired
    private EventScheduler eventScheduler;

    @Value("${db.sqliteUrl}")
    private String sqliteUrl;

    private void migrateDb() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(sqliteUrl, null, null);
        flyway.migrate();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            jda.addEventListener(commandListener);
            jda.addEventListener(reactionListener);
            migrateDb();
            eventScheduler.scheduleAll();
            jda.getPresence().setGame(Game.playing("say \"@Ninbot help\" for list of commands"));
        };
    }
}

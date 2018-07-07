package com.nincraft.ninbot;

import com.nincraft.ninbot.components.common.AbstractScheduler;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@Log4j2
@ComponentScan({"com.nincraft.ninbot"})
public class Ninbot {

    @Autowired
    private JDA jda;

    @Autowired
    private List<AbstractScheduler> schedulers;

    @Value("${db.sqliteUrl}")
    private String dbUrl;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            for (val scheduler : schedulers) {
                scheduler.scheduleAll(jda);
            }
            jda.getPresence().setGame(Game.playing("say \"@Ninbot help\" for list of commands"));
        };
    }
}

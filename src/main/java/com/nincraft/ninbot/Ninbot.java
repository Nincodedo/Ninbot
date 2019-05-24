package com.nincraft.ninbot;

import com.nincraft.ninbot.components.common.Schedulable;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
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
    private List<Schedulable> schedulableList;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            log.info("Joined {} server(s)", jda.getGuilds().size());
            schedulableList.forEach(schedule -> schedule.scheduleAll(jda));
            jda.getPresence().setActivity(Activity.playing("say \"@Ninbot help\" for list of commands"));
        };
    }
}

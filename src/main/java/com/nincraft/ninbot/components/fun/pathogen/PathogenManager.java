package com.nincraft.ninbot.components.fun.pathogen;

import com.nincraft.ninbot.components.common.Emojis;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

@Log4j2
@Component
@Controller
@RequestMapping("/pathogen")
public class PathogenManager {

    private Random todayRandom;
    private Random random;
    private String roleName = "infected";

    public PathogenManager() {
        this.todayRandom = new Random(new Date().getTime());
        this.random = new Random();

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setRandomSeed() {
        this.todayRandom = new Random(LocalDate.now().toEpochDay());
    }


    public void spread(Guild guild, Map<User, Message> possiblePathogenVictims) {
        val infectedRoles = guild.getRolesByName(roleName, true);
        if (infectedRoles.isEmpty()) {
            return;
        }
        val infectedRole = infectedRoles.get(0);
        //Infect chance
        possiblePathogenVictims.keySet()
                .stream()
                .filter(user -> !user.isBot() && !isInfectedMember(guild.getMember(user))
                        && random.nextInt(100) < 60)
                .forEach(user -> {
                    guild.addRoleToMember(guild.getMember(user), infectedRole).queue();
                    possiblePathogenVictims.get(user).addReaction(Emojis.SICK_FACE).queue();
                });
    }

    boolean isInfectedMember(Member member) {
        if (member == null) {
            return false;
        }
        return member.getRoles().stream()
                .anyMatch(role -> roleName.equalsIgnoreCase(role.getName()));
    }

    @GetMapping("/wordlist")
    @ResponseBody
    public List<String> getWordList() {
        setRandomSeed();
        try {
            List<String> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                    .getResourceAsStream("listOfCommonWords.txt")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    list.add(line);
                    line = null;
                }
            }
            List<String> shortList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                shortList.add(list.get(todayRandom.nextInt(list.size())));
            }
            return shortList;

        } catch (IOException e) {
            log.error("Failed to read list of common words", e);
            return new ArrayList<>();
        }
    }
}

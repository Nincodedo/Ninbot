package com.nincraft.ninbot.components.fun.pathogen;

import com.nincraft.ninbot.components.common.Emojis;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Component
@Controller
@RequestMapping("/pathogen")
public class PathogenManager {

    private PathogenAuditRepository pathogenAuditRepository;
    private Random todayRandom;
    private Random random;
    private String roleName = "infected";
    @Getter
    private int wordListLength = 30;
    private boolean healingWeek;

    public PathogenManager(PathogenAuditRepository pathogenAuditRepository) {
        this.pathogenAuditRepository = pathogenAuditRepository;
        this.todayRandom = new Random(new Date().getTime());
        this.random = new Random();
        this.healingWeek = determineIfHealingWeek();
    }

    private boolean determineIfHealingWeek() {
        return LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) % 2 == 0;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setRandomSeed() {
        val seed = LocalDate.now().toEpochDay();
        this.todayRandom = new Random(seed);
        this.healingWeek = determineIfHealingWeek();
        PathogenAudit audit = new PathogenAudit();
        audit.setAction("setRandomSeed");
        audit.setDescription(String.format("Seed used %s, healing week %s", seed, healingWeek));
        audit.setCreationDate(LocalDateTime.now());
        audit.setCreatedBy("ninbot");
    }

    public void setRandomSeed(long seed) {
        this.todayRandom = new Random(seed);
    }


    public void spread(Guild guild, Map<User, Message> possiblePathogenVictims, int messageAffectChance) {
        val infectedRoles = guild.getRolesByName(roleName, true);
        if (infectedRoles.isEmpty()) {
            return;
        }
        val infectedRole = infectedRoles.get(0);
        //Infect/heal chance
        possiblePathogenVictims.keySet()
                .stream()
                .filter(user -> !user.isBot() && random.nextInt(100) < messageAffectChance)
                .forEach(user -> {
                    if (healingWeek) {
                        guild.removeRoleFromMember(guild.getMember(user), infectedRole).queue(aVoid -> {
                            //Successfully removed role, so add healing reaction
                            possiblePathogenVictims.get(user).addReaction(Emojis.PILLS).queue();
                        });

                    } else {
                        guild.addRoleToMember(guild.getMember(user), infectedRole).queue(aVoid -> {
                            //Successfully added role, so add infected reaction
                            possiblePathogenVictims.get(user).addReaction(Emojis.SICK_FACE).queue();
                        });
                    }
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
    public Set<String> getWordList() {
        setRandomSeed();
        try {
            List<String> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                    .getResourceAsStream("listOfCommonWords.txt")))) {
                String line;
                while ((line = br.readLine()) != null) {
                    list.add(line);
                }
            }
            return IntStream.range(0, wordListLength)
                    .mapToObj(i -> list.remove(todayRandom.nextInt(list.size())))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to read list of common words", e);
            return new HashSet<>();
        }
    }

    boolean messageContainsSecretWordOfTheDay(String contentStripped) {
        List<String> messageList = Arrays.asList(contentStripped.split("\\s+"));
        return getWordList()
                .stream()
                .anyMatch(secretWord -> messageList.stream().anyMatch(secretWord::equalsIgnoreCase));
    }

    boolean isSpreadableEvent(MessageReceivedEvent event) {
        return messageContainsSecretWordOfTheDay(event.getMessage().getContentStripped());
    }
}

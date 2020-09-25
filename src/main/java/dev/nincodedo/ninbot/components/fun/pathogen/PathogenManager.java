package dev.nincodedo.ninbot.components.fun.pathogen;

import dev.nincodedo.ninbot.components.common.Emojis;
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
public class PathogenManager {

    private PathogenAuditRepository pathogenAuditRepository;
    private PathogenUserService pathogenUserService;
    private Random todayRandom;
    private Random random;
    @Getter
    private int wordListLength = 15;
    private boolean healingWeek;

    public PathogenManager(PathogenUserService pathogenUserService,
            PathogenAuditRepository pathogenAuditRepository) {
        this.pathogenAuditRepository = pathogenAuditRepository;
        this.pathogenUserService = pathogenUserService;
        this.todayRandom = new Random(new Date().getTime());
        this.random = new Random();
        this.healingWeek = determineIfHealingWeek();
    }

    private boolean determineIfHealingWeek() {
        return healingWeekId() % 2 == 0;
    }

    private int healingWeekId() {
        return LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setRandomSeed() {
        setRandomSeed(true);
    }

    public void setRandomSeed(boolean doAuditAction) {
        setRandomSeed(doAuditAction, null);
    }

    public void setRandomSeed(boolean doAuditAction, LocalDate localDate) {
        long seed = localDate == null ? LocalDate.now().toEpochDay() : localDate.toEpochDay();
        this.todayRandom = new Random(seed);
        this.healingWeek = determineIfHealingWeek();
        if (doAuditAction) {
            PathogenAudit audit = new PathogenAudit();
            audit.setAction("setRandomSeed");
            audit.setDescription(String.format("Seed used %s, healing week %s", seed, healingWeek));
            audit.setCreationDate(LocalDateTime.now());
            audit.setCreatedBy("ninbot");
            audit.setWeekId(healingWeekId());
            pathogenAuditRepository.save(audit);
        }
    }

    public void setRandomSeed(long seed) {
        this.todayRandom = new Random(seed);
    }


    public void spread(Guild guild, User spreadSource,
            Map<User, Message> possiblePathogenVictims, int messageAffectChance) {
        val infectedRoles = guild.getRolesByName(PathogenConfig.getROLE_NAME(), true);
        if (infectedRoles.isEmpty()) {
            return;
        }
        val infectedRole = infectedRoles.get(0);
        //Infect/heal chance
        possiblePathogenVictims.keySet()
                .stream()
                .filter(user -> !user.isBot() && random.nextInt(100) < messageAffectChance)
                .forEach(user -> {
                    val channelId = possiblePathogenVictims.get(user).getChannel().getId();
                    val pathogenUser = pathogenUserService.getByUserIdAndServerId(user.getId(),
                            guild.getId());
                    if (healingWeek) {
                        guild.removeRoleFromMember(guild.getMember(user), infectedRole).queue(aVoid -> {
                            //Successfully removed role, so add healing reaction
                            possiblePathogenVictims.get(user).addReaction(Emojis.PILLS).queue();
                            auditAction(spreadSource, channelId, user, "healing",
                                    "Healed user %s in channel %s");
                            pathogenUserService.uninfectedUser(pathogenUser, user.getId(), guild.getId());
                        });
                    } else {
                        guild.addRoleToMember(guild.getMember(user), infectedRole).queue(aVoid -> {
                            //Successfully added role, so add infected reaction
                            possiblePathogenVictims.get(user).addReaction(Emojis.SICK_FACE).queue();
                            auditAction(spreadSource, channelId, user, "infecting", "Infected user %s "
                                    + "in channel %s");
                            pathogenUserService.infectedUser(pathogenUser, user.getId(), guild.getId());
                        });
                    }
                });
    }

    private void auditAction(User spreadSource, String channelId, User targetUser, String action,
            String description) {
        PathogenAudit audit = new PathogenAudit();
        audit.setAction(action);
        audit.setDescription(String.format(description, targetUser.getId(),
                channelId));
        audit.setCreationDate(LocalDateTime.now());
        audit.setCreatedBy(spreadSource.getId());
        audit.setWeekId(healingWeekId());
        pathogenAuditRepository.save(audit);
    }

    boolean isInfectedMember(Member member) {
        if (member == null) {
            return false;
        }
        return member.getRoles().stream()
                .anyMatch(role -> PathogenConfig.getROLE_NAME().equalsIgnoreCase(role.getName()));
    }

    public Set<String> getWordList() {
        setRandomSeed(false);
        List<String> list = readWordList();
        return IntStream.range(0, wordListLength)
                .mapToObj(i -> list.remove(todayRandom.nextInt(list.size())))
                .collect(Collectors.toSet());
    }

    public Set<String> getWordList(String date) {
        LocalDate localDate = LocalDate.parse(date);
        setRandomSeed(false, localDate);
        List<String> list = readWordList();
        return IntStream.range(0, wordListLength)
                .mapToObj(i -> list.remove(todayRandom.nextInt(list.size())))
                .collect(Collectors.toSet());
    }

    private List<String> readWordList() {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                .getResourceAsStream("listOfCommonWords.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            log.error("Failed to read common word file", e);
            return new ArrayList<>();
        }
        return list;
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

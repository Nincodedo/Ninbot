package dev.nincodedo.ninbot.components.pathogen;

import dev.nincodedo.ninbot.components.pathogen.audit.PathogenAudit;
import dev.nincodedo.ninbot.components.pathogen.audit.PathogenAuditRepository;
import dev.nincodedo.ninbot.components.pathogen.user.PathogenUserService;
import dev.nincodedo.nincord.Emojis;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
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
            audit.setDescription("Seed used %s, healing week %s".formatted(seed, healingWeek));
            audit.getAudit().setCreatedBy("ninbot");
            audit.setWeekId(healingWeekId());
            pathogenAuditRepository.save(audit);
        }
    }

    public void setRandomSeed(long seed) {
        this.todayRandom = new Random(seed);
    }


    public void spread(Guild guild, User spreadSource,
            Map<User, Message> possiblePathogenVictims, int messageAffectChance) {
        var infectedRoles = guild.getRolesByName(PathogenConfig.getINFECTED_ROLE_NAME(), true);
        if (infectedRoles.isEmpty()) {
            return;
        }
        var infectedRole = infectedRoles.get(0);
        //Infect/heal chance
        possiblePathogenVictims.keySet()
                .stream()
                .filter(user -> !user.isBot() && random.nextInt(100) < messageAffectChance)
                .forEach(user -> {
                    var pathogenTargetMessage = possiblePathogenVictims.get(user);
                    var channelId = possiblePathogenVictims.get(user).getChannel().getId();
                    var pathogenUser = pathogenUserService.getByUserIdAndServerId(user.getId(),
                            guild.getId());
                    var vaccinatedRoleList = guild.getRolesByName(PathogenConfig.getVACCINATED_ROLE_NAME(), false);
                    var member = guild.getMember(user);
                    if (member == null) {
                        return;
                    }
                    if (healingWeek) {
                        if (vaccinatedRoleList.isEmpty() || !member.getRoles().contains(vaccinatedRoleList.get(0))) {
                            //Successfully removed role and add healing reaction, then do audit
                            guild.removeRoleFromMember(member, infectedRole)
                                    .and(pathogenTargetMessage.addReaction(Emoji.fromFormatted(Emojis.PILLS)))
                                    .queue(success -> {
                                        auditAction(spreadSource, channelId, user, "healing",
                                                "Healed user %s in channel %s");
                                        pathogenUserService.uninfectedUser(pathogenUser, user.getId(), guild.getId());
                                    });
                        }
                    } else if (vaccinatedRoleList.isEmpty() || !member.getRoles().contains(vaccinatedRoleList.get(0))) {
                        //Successfully added role and add infected reaction, then do audit
                        guild.addRoleToMember(member, infectedRole)
                                .and(pathogenTargetMessage.addReaction(Emoji.fromFormatted(Emojis.SICK_FACE)))
                                .queue(success -> {
                                    auditAction(spreadSource, channelId, user, "infecting", "Infected user %s "
                                            + "in channel %s");
                                    pathogenUserService.infectedUser(pathogenUser, user.getId(), guild.getId());
                                });
                    } else if (member.getRoles().contains(vaccinatedRoleList.get(0))) {
                        pathogenTargetMessage.addReaction(Emoji.fromFormatted(Emojis.getRandomDoctorEmoji())).queue();
                        pathogenTargetMessage.addReaction(Emoji.fromFormatted(Emojis.THUMBS_UP)).queue();
                        auditAction(spreadSource, channelId, user, "vaccine-block", "Vaccine blocked user %s "
                                + "in channel %s");
                    }
                });
    }

    private void auditAction(User spreadSource, String channelId, User targetUser, String action,
            String description) {
        PathogenAudit audit = new PathogenAudit();
        audit.setAction(action);
        audit.setDescription(description.formatted(targetUser.getId(), channelId));
        audit.getAudit().setCreatedBy(spreadSource.getId());
        audit.setWeekId(healingWeekId());
        pathogenAuditRepository.save(audit);
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

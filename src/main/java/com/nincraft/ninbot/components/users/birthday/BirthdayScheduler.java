package com.nincraft.ninbot.components.users.birthday;

import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.GenericAnnounce;
import com.nincraft.ninbot.components.common.Schedulable;
import com.nincraft.ninbot.components.users.NinbotUser;
import com.nincraft.ninbot.components.users.UserRepository;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class BirthdayScheduler implements Schedulable {

    private UserRepository userRepository;

    public BirthdayScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void scheduleAll(ShardManager shardManager) {
        new Timer().scheduleAtFixedRate(new Scheduler(shardManager), new Date(), TimeUnit.DAYS.toMillis(1));
    }

    void scheduleBirthdayAnnouncement(NinbotUser ninbotUser, ShardManager shardManager) {
        log.info("Checking if {} birthday should be scheduled", ninbotUser.getUserId());
        val calendar = GregorianCalendar.from(ZonedDateTime.from(ninbotUser.getBirthday().toInstant()));
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Date birthdateThisYear = null;
        try {
            birthdateThisYear = new SimpleDateFormat("MMddyyyy").parse(getStringDate(monthOfYear, dayOfMonth,
                    year));
        } catch (ParseException e) {
            log.error("Failed to parse date", e);
        }
        if (birthdateThisYear != null && DateUtils.isSameDay(birthdateThisYear, Date.from(LocalDate.now()
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()))) {
            log.info("Scheduling birthday announcement for {}", ninbotUser.getUserId());
            Message birthdayMessage = buildMessage(ninbotUser, shardManager);
            val announcementChannelId = shardManager.getGuildById(ninbotUser.getServerId()).getDefaultChannel().getId();
            new Timer().schedule(new GenericAnnounce(shardManager, announcementChannelId, birthdayMessage),
                    Date.from(LocalDate.now(ZoneId.systemDefault())
                            .atStartOfDay(ZoneId.systemDefault())
                            .plus(1, ChronoUnit.DAYS)
                            .toInstant()));
        }
    }

    private String getStringDate(int monthOfYear, int dayOfMonth, int year) {
        String date = String.format("%02d", monthOfYear);
        date += String.format("%02d", dayOfMonth);
        date += year;
        return date;
    }

    private Message buildMessage(NinbotUser ninbotUser, ShardManager shardManager) {
        MessageBuilder messageBuilder = new MessageBuilder();
        val user = shardManager.getUserById(ninbotUser.getId());
        messageBuilder.append("It's ");
        messageBuilder.append(user);
        messageBuilder.append(" birthday today! ");
        messageBuilder.append(
                Emojis.BIRTHDAY_CAKE + " " + Emojis.PARTY_FACE + " " + Emojis.BALLOON + " " + Emojis.PARTY_POPPER);
        return messageBuilder.build();
    }

    class Scheduler extends TimerTask {
        private ShardManager shardManager;

        Scheduler(ShardManager shardManager) {
            this.shardManager = shardManager;
        }

        @Override
        public void run() {
            userRepository.findAll().forEach(ninbotUser -> scheduleBirthdayAnnouncement(ninbotUser, shardManager));
        }
    }
}

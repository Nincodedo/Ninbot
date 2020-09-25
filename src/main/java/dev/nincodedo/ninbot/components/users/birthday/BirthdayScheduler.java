package dev.nincodedo.ninbot.components.users.birthday;

import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.GenericAnnounce;
import dev.nincodedo.ninbot.components.common.Schedulable;
import dev.nincodedo.ninbot.components.users.NinbotUser;
import dev.nincodedo.ninbot.components.users.UserRepository;
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
        log.trace("Checking if {} birthday should be scheduled", ninbotUser.getUserId());
        val birthdayString = ninbotUser.getBirthday();
        val birthdayOptional = getDateWithFormat(birthdayString);
        if (birthdayOptional.isPresent()) {
            val birthday = birthdayOptional.get();
            val calendar = GregorianCalendar.from(ZonedDateTime.from(birthday.toInstant()
                    .atZone(ZoneId.systemDefault())));
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            //apparently months start at 0
            int monthOfYear = calendar.get(Calendar.MONTH) + 1;
            //get THIS year because otherwise that's stupid
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
                val announcementChannelId = shardManager.getGuildById(ninbotUser.getServerId())
                        .getDefaultChannel()
                        .getId();
                new Timer().schedule(new GenericAnnounce(shardManager, announcementChannelId, birthdayMessage),
                        Date.from(LocalDate.now(ZoneId.systemDefault())
                                .atStartOfDay(ZoneId.systemDefault())
                                .plus(1, ChronoUnit.DAYS)
                                .toInstant()));
            }
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

    private Optional<Date> getDateWithFormat(String birthdayString) {
        String birthdayFormat1 = "MM/dd/yyyy";
        String birthdayFormat2 = "MM/dd";
        try {
            return Optional.of(new SimpleDateFormat(birthdayFormat1).parse(birthdayString));
        } catch (ParseException e) {
            log.trace("Failed to parse date {} with format {}, attempting with {}", birthdayString, birthdayFormat1,
                    birthdayFormat2);
            try {
                return Optional.of(new SimpleDateFormat(birthdayFormat2).parse(birthdayString));
            } catch (ParseException ex) {
                log.error("Failed to parse date {} with format {}, nothing left to try :(", birthdayString,
                        birthdayFormat2);
                return Optional.empty();
            }
        }
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

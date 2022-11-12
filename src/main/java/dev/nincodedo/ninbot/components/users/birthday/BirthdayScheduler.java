package dev.nincodedo.ninbot.components.users.birthday;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.Schedulable;
import dev.nincodedo.ninbot.common.message.GenericAnnounce;
import dev.nincodedo.ninbot.components.users.NinbotUser;
import dev.nincodedo.ninbot.components.users.UserService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BirthdayScheduler extends Schedulable<NinbotUser, UserService> {

    private UserService userService;

    public BirthdayScheduler(UserService userService,
            @Qualifier("schedulerThreadPool") ExecutorService executorService) {
        super(executorService);
        this.userService = userService;
    }

    @Override
    protected String getSchedulerName() {
        return "birthday";
    }

    @Override
    public void scheduleAll(ShardManager shardManager) {
        getTimer().scheduleAtFixedRate(new Scheduler(shardManager), new Date(), TimeUnit.DAYS.toMillis(1));
    }

    @Override
    public UserService getScheduler() {
        return userService;
    }

    @Override
    public void scheduleOne(NinbotUser ninbotUser, ShardManager shardManager) {
        log.trace("Checking if {} birthday should be scheduled", ninbotUser.getUserId());
        var birthdayString = ninbotUser.getBirthday();
        var birthdayOptional = getDateWithFormat(birthdayString);
        if (birthdayOptional.isPresent() && Boolean.TRUE.equals(ninbotUser.getAnnounceBirthday())) {
            var birthday = birthdayOptional.get();
            var calendar = GregorianCalendar.from(ZonedDateTime.from(birthday.toInstant()
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
            if (isBirthdayTomorrow(birthdateThisYear)) {
                log.trace("Scheduling birthday announcement for {}", ninbotUser.getUserId());
                MessageCreateData birthdayMessage = buildMessage(ninbotUser, shardManager);
                var guild = shardManager.getGuildById(ninbotUser.getServerId());
                if (guild == null) {
                    return;
                }
                var defaultChannel = guild.getDefaultChannel();
                if (defaultChannel == null) {
                    return;
                }
                var announcementChannelId = defaultChannel.getId();
                getTimer().schedule(new GenericAnnounce(shardManager, announcementChannelId, birthdayMessage),
                        Date.from(LocalDate.now(ZoneId.systemDefault())
                                .atStartOfDay(ZoneId.systemDefault())
                                .plus(1, ChronoUnit.DAYS)
                                .toInstant()));
            }
        }
    }

    private boolean isBirthdayTomorrow(Date birthdateThisYear) {
        return birthdateThisYear != null && DateUtils.isSameDay(birthdateThisYear, Date.from(LocalDate.now()
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()));
    }

    private String getStringDate(int monthOfYear, int dayOfMonth, int year) {
        String date = String.format("%02d", monthOfYear);
        date += String.format("%02d", dayOfMonth);
        date += year;
        return date;
    }

    private MessageCreateData buildMessage(NinbotUser ninbotUser, ShardManager shardManager) {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        var user = shardManager.getUserById(ninbotUser.getId());
        messageBuilder.addContent("It's ");
        messageBuilder.addContent(user.getName());
        messageBuilder.addContent(" birthday today! ");
        messageBuilder.addContent(
                Emojis.BIRTHDAY_CAKE + " " + Emojis.PARTY_FACE + " " + Emojis.BALLOON + " " + Emojis.PARTY_POPPER);
        return messageBuilder.build();
    }

    private Optional<Date> getDateWithFormat(String birthdayString) {
        String birthdayFormat1 = "MM/dd/yyyy";
        String birthdayFormat2 = "MM/dd";
        try {
            return Optional.of(new SimpleDateFormat(birthdayFormat1).parse(birthdayString));
        } catch (ParseException e) {
            log.warn("Failed to parse date {} with format {}, attempting with {}", birthdayString, birthdayFormat1,
                    birthdayFormat2);
            try {
                return Optional.of(new SimpleDateFormat(birthdayFormat2).parse(birthdayString));
            } catch (ParseException ex) {
                log.warn("Failed to parse date {} with format {}, nothing left to try :(", birthdayString,
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
            userService.findAllOpenItems().forEach(ninbotUser -> scheduleOne(ninbotUser, shardManager));
        }
    }
}

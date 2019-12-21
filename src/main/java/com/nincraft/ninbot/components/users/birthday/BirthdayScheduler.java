package com.nincraft.ninbot.components.users.birthday;

import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.GenericAnnounce;
import com.nincraft.ninbot.components.common.Schedulable;
import com.nincraft.ninbot.components.users.NinbotUser;
import com.nincraft.ninbot.components.users.UserRepository;
import lombok.val;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Component
public class BirthdayScheduler implements Schedulable {

    private UserRepository userRepository;

    public BirthdayScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void scheduleAll(ShardManager shardManager) {
        new Timer().scheduleAtFixedRate(new Scheduler(shardManager), new Date(), TimeUnit.DAYS.toMillis(1));
    }

    void scheduleBirthdayAnnouncement(NinbotUser ninbotUser, ShardManager shardManager) {
        if (DateUtils.isSameDay(ninbotUser.getBirthday(), Date.from(LocalDate.now()
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()))) {
            Message birthdayMessage = buildMessage(ninbotUser, shardManager);
            new Timer().schedule(new GenericAnnounce(shardManager, "497392348192571392", birthdayMessage),
                    Date.from(LocalDate.now(ZoneId.systemDefault())
                            .atStartOfDay(ZoneId.systemDefault())
                            .plus(1, ChronoUnit.DAYS)
                            .toInstant()));
        }
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

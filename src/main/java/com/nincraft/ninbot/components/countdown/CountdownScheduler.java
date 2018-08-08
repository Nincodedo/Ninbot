package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.common.GenericAnnounce;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class CountdownScheduler {

    private CountdownDao countdownDao;
    private ConfigService configService;

    public CountdownScheduler(CountdownDao countdownDao, ConfigService configService) {
        this.countdownDao = countdownDao;
        this.configService = configService;
    }

    public void scheduleAll(JDA jda) {
        new Timer().scheduleAtFixedRate(new Scheduler(jda), new Date(), TimeUnit.DAYS.toMillis(1));
    }

    void scheduleOne(Countdown countdown, JDA jda) {
        val countdownDate = countdown.getEventDate();
        val tomorrowDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        if (countdownDate.isEqual(tomorrowDate) || countdownDate.isAfter(tomorrowDate)) {
            val dayDifference = ChronoUnit.DAYS.between(tomorrowDate, countdownDate);
            val countdownMessage = countdown.buildMessage(dayDifference);
            val announceChannelOptional = Optional.ofNullable(countdown.getChannelId());
            val configChannelOptional = configService.getSingleValueByName(countdown.getServerId(), ConfigConstants.ANNOUNCE_CHANNEL);
            String announceChannel;
            if (announceChannelOptional.isPresent()) {
                announceChannel = announceChannelOptional.get();
            } else if (configChannelOptional.isPresent()) {
                announceChannel = configChannelOptional.get();
            } else {
                log.warn("Could not schedule countdown {}. No announcement channel was configured for server {} or this countdown", countdown.getName(), countdown.getServerId());
                return;
            }
            new Timer().schedule(new GenericAnnounce(jda, announceChannel, countdownMessage),
                    Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plus(1, ChronoUnit.DAYS).toInstant()));
        } else {
            log.debug("Countdown {} is past, removing", countdown.getName());
            countdownDao.removeObject(countdown);
        }
    }

    class Scheduler extends TimerTask {

        private JDA jda;

        Scheduler(JDA jda) {
            this.jda = jda;
        }

        @Override
        public void run() {
            countdownDao.getAllObjects().forEach(countdown -> scheduleOne(countdown, jda));
        }
    }
}

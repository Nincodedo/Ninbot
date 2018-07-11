package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.common.GenericAnnounce;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;

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
        countdownDao.getAllObjects().forEach(countdown -> scheduleOne(countdown, jda));
    }

    private void scheduleOne(Countdown countdown, JDA jda) {
        val countdownDate = countdown.getEventDate();
        val tomorrowDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        if (countdownDate.isAfter(tomorrowDate)) {
            val dayDifference = ChronoUnit.DAYS.between(countdownDate, tomorrowDate);
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
            Timer timer = new Timer();
            timer.schedule(new GenericAnnounce(jda, announceChannel, countdownMessage), Date.from(LocalDate.now().atStartOfDay().plus(1, ChronoUnit.DAYS).toInstant(ZoneOffset.of(ZoneOffset.systemDefault().getId()))));
        }
    }
}

package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.common.GenericAnnounce;
import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.common.Schedulable;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class CountdownScheduler implements Schedulable {

    private CountdownRepository countdownRepository;
    private ConfigService configService;
    private LocaleService localeService;

    public CountdownScheduler(CountdownRepository countdownRepository, ConfigService configService,
            LocaleService localeService) {
        this.countdownRepository = countdownRepository;
        this.configService = configService;
        this.localeService = localeService;
    }

    public void scheduleAll(ShardManager shardManager) {
        new Timer().scheduleAtFixedRate(new Scheduler(shardManager), new Date(), TimeUnit.DAYS.toMillis(1));
    }

    void scheduleOne(Countdown countdown, ShardManager shardManager) {
        val countdownDate = countdown.getEventDate();
        val tomorrowDate = LocalDate.now(countdownDate.getZone())
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .atZone(countdownDate.getZone());
        if (countdownDate.isEqual(tomorrowDate) || countdownDate.isAfter(tomorrowDate)) {
            val dayDifference = countdown.getDayDifference();
            if (dayDifference > 7 && dayDifference % 7 != 0) {
                log.debug("Not scheduling countdown {} because it is {} days away", countdown.getName(), dayDifference);
                return;
            }
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang",
                    localeService.getLocale(countdown.getServerId()));
            countdown.setResourceBundle(resourceBundle);
            val countdownMessage = countdown.buildMessage();
            val announceChannelOptional = Optional.ofNullable(countdown.getChannelId());
            val configChannelOptional = configService.getSingleValueByName(countdown.getServerId(),
                    ConfigConstants.ANNOUNCE_CHANNEL);
            String announceChannel;
            if (announceChannelOptional.isPresent()) {
                announceChannel = announceChannelOptional.get();
            } else if (configChannelOptional.isPresent()) {
                announceChannel = configChannelOptional.get();
            } else {
                log.warn("Could not schedule countdown {}. No announcement channel was configured for server {} or "
                        + "this countdown", countdown
                        .getName(), countdown.getServerId());
                return;
            }
            new Timer().schedule(new GenericAnnounce(shardManager, announceChannel, countdownMessage),
                    Date.from(LocalDate.now(countdownDate.getZone())
                            .atStartOfDay(countdownDate.getZone())
                            .plus(1, ChronoUnit.DAYS)
                            .toInstant()));
        } else {
            log.debug("Countdown {} is past, removing", countdown.getName());
            countdownRepository.delete(countdown);
        }
    }

    class Scheduler extends TimerTask {

        private ShardManager shardManager;

        Scheduler(ShardManager shardManager) {
            this.shardManager = shardManager;
        }

        @Override
        public void run() {
            countdownRepository.findAll().forEach(countdown -> scheduleOne(countdown, shardManager));
        }
    }
}

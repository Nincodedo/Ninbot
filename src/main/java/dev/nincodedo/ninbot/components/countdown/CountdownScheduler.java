package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.Schedulable;
import dev.nincodedo.ninbot.common.message.GenericAnnounce;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CountdownScheduler implements Schedulable {

    private CountdownRepository countdownRepository;
    private ConfigService configService;

    public CountdownScheduler(CountdownRepository countdownRepository, ConfigService configService) {
        this.countdownRepository = countdownRepository;
        this.configService = configService;
    }

    public void scheduleAll(ShardManager shardManager) {
        new Timer().scheduleAtFixedRate(new Scheduler(shardManager), new Date(), TimeUnit.DAYS.toMillis(1));
    }

    void scheduleOne(Countdown countdown, ShardManager shardManager) {
        var countdownDate = countdown.getEventDate();
        var tomorrowDate = LocalDate.now(countdownDate.getZone())
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .atZone(countdownDate.getZone());
        if (countdownDate.isEqual(tomorrowDate) || countdownDate.isAfter(tomorrowDate)) {
            var dayDifference = countdown.getDayDifference();
            if (isAnnounceable(dayDifference)) {
                log.debug("Not scheduling countdown {} because it is {} days away", countdown.getName(), dayDifference);
                return;
            }

            ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(shardManager.getGuildById(countdown
                    .getServerId()));
            countdown.setResourceBundle(resourceBundle);
            var countdownMessage = countdown.buildMessage();
            var announceChannelOptional = Optional.ofNullable(countdown.getChannelId());
            var configChannelOptional = configService.getSingleValueByName(countdown.getServerId(),
                    ConfigConstants.ANNOUNCE_CHANNEL);
            String announceChannel;
            if (announceChannelOptional.isPresent()) {
                announceChannel = announceChannelOptional.get();
            } else if (configChannelOptional.isPresent()) {
                announceChannel = configChannelOptional.get();
            } else {
                log.warn("Could not schedule countdown {}. No announcement channel was configured for server {} or "
                        + "this countdown", countdown
                        .getId(), countdown.getServerId());
                return;
            }
            new Timer().schedule(new GenericAnnounce(shardManager, announceChannel, countdownMessage),
                    Date.from(LocalDate.now(countdownDate.getZone())
                            .atStartOfDay(countdownDate.getZone())
                            .plus(1, ChronoUnit.DAYS)
                            .toInstant()));
        } else {
            log.debug("Countdown {} is past, removing", countdown.getId());
            countdownRepository.delete(countdown);
        }
    }

    private boolean isAnnounceable(long dayDifference) {
        return dayDifference > 100 && dayDifference % 100 != 0 || dayDifference > 7 && dayDifference % 7 != 0;
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

package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.Schedulable;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.message.GenericAnnounce;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class CountdownScheduler extends Schedulable<Countdown, CountdownService> {

    private CountdownService countdownService;
    private ConfigService configService;

    public CountdownScheduler(CountdownService countdownService, ConfigService configService, @Qualifier(
            "schedulerThreadPool") ExecutorService executorService) {
        super(executorService);
        this.countdownService = countdownService;
        this.configService = configService;
    }

    @Override
    protected String getSchedulerName() {
        return "countdown";
    }

    public void scheduleOne(Countdown countdown, ShardManager shardManager) {
        var countdownDate = countdown.getEventDate();
        var tomorrowDate = LocalDate.now(countdownDate.getZone())
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .atZone(countdownDate.getZone());
        if (countdownDate.isEqual(tomorrowDate) || countdownDate.isAfter(tomorrowDate)) {
            var dayDifference = countdown.getDayDifference();
            if (isUnannounceable(dayDifference)) {
                log.debug("Not scheduling countdown {} because it is {} days away", countdown.getName(), dayDifference);
                return;
            }

            ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(shardManager.getGuildById(countdown
                    .getServerId()));
            countdown.setResourceBundle(resourceBundle);
            var guild = shardManager.getGuildById(countdown.getServerId());
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
                        .getId(), FormatLogObject.guildName(guild));
                return;
            }
            getTimer().schedule(new GenericAnnounce(shardManager, announceChannel, countdownMessage),
                    Date.from(LocalDate.now(countdownDate.getZone())
                            .atStartOfDay(countdownDate.getZone())
                            .plus(1, ChronoUnit.DAYS)
                            .toInstant()));
        } else {
            log.debug("Countdown {} is past, removing", countdown.getId());
            countdownService.delete(countdown);
        }
    }

    @Override
    public CountdownService getSchedulerService() {
        return countdownService;
    }

    private boolean isUnannounceable(long dayDifference) {
        return dayDifference > 1000 && dayDifference % 1000 != 0
                || dayDifference < 1000 && dayDifference > 100 && dayDifference % 100 != 0
                || dayDifference < 100 && dayDifference > 7 && dayDifference % 7 != 0;
    }
}

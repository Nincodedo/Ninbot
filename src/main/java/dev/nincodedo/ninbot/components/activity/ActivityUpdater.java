package dev.nincodedo.ninbot.components.activity;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class ActivityUpdater {

    private final ShardManager shardManager;
    private final Random random;
    private List<String> activityStatusList;
    private ActivityStatusRepository activityStatusRepository;

    public ActivityUpdater(ShardManager shardManager, ActivityStatusRepository activityStatusRepository) {
        this.shardManager = shardManager;
        this.random = new SecureRandom();
        this.activityStatusRepository = activityStatusRepository;
        updateStatusList();
        setNinbotActivity();
    }

    @Scheduled(fixedRate = 12, timeUnit = TimeUnit.HOURS)
    private void updateStatusList() {
        activityStatusList = activityStatusRepository.findAll()
                .stream()
                .map(ActivityStatus::getStatus)
                .toList();
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    private void setNinbotActivity() {
        if (!activityStatusList.isEmpty()) {
            var status = activityStatusList.get(random.nextInt(activityStatusList.size()));
            shardManager.setActivity(Activity.playing(status));
        }
    }
}

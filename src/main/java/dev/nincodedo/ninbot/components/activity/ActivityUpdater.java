package dev.nincodedo.ninbot.components.activity;

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
    private List<ActivityStatus> activityStatusList;
    private ActivityStatusRepository activityStatusRepository;

    public ActivityUpdater(ShardManager shardManager, ActivityStatusRepository activityStatusRepository) {
        this.shardManager = shardManager;
        this.random = new SecureRandom();
        this.activityStatusRepository = activityStatusRepository;
        updateStatusList();
        setNinbotActivity();
    }

    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    protected void updateStatusList() {
        activityStatusList = activityStatusRepository.findAll();
    }

    @Scheduled(fixedDelay = 4, timeUnit = TimeUnit.HOURS)
    protected void setNinbotActivity() {
        if (!activityStatusList.isEmpty()) {
            var activityStatus = activityStatusList.get(random.nextInt(activityStatusList.size()));
            shardManager.setActivity(activityStatus.getAsActivity());
        }
    }
}

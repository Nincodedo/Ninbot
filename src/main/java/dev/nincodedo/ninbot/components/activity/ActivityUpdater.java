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
    private final List<String> activityStatusList = List.of(
            "Check out my fancy new slash commands!",
            "New status, who this?"
    );
    private final Random random;

    public ActivityUpdater(ShardManager shardManager) {
        this.shardManager = shardManager;
        this.random = new SecureRandom();
        setNinbotActivity();
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    private void setNinbotActivity() {
        var status = activityStatusList.get(random.nextInt(activityStatusList.size()));
        shardManager.setActivity(Activity.playing(status));
    }
}

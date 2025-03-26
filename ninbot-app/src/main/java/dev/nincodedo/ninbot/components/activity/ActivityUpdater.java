package dev.nincodedo.ninbot.components.activity;

import dev.nincodedo.nincord.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ActivityUpdater {

    private final ShardManager shardManager;
    protected List<ActivityStatus> activityStatusList;
    private ActivityStatusRepository activityStatusRepository;

    public ActivityUpdater(ShardManager shardManager, ActivityStatusRepository activityStatusRepository) {
        this.shardManager = shardManager;
        this.activityStatusList = new ArrayList<>();
        this.activityStatusRepository = activityStatusRepository;
    }

    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    protected void updateNinbotActivity() {
        if (activityStatusList.isEmpty()) {
            activityStatusList.addAll(activityStatusRepository.findAll());
            activityStatusList = activityStatusList.stream().sorted(StreamUtils.shuffle()).collect(Collectors.toList());
        }
        if (!activityStatusList.isEmpty()) {
            var activityStatus = activityStatusList.remove(0);
            shardManager.setActivity(activityStatus.getAsActivity());
        } else {
            log.error("Failed to retrieve activity status list");
        }
    }
}

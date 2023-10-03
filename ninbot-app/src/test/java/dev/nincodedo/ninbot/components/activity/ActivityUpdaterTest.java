package dev.nincodedo.ninbot.components.activity;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityUpdaterTest {

    @Mock
    ShardManager shardManager;

    @Mock
    ActivityStatusRepository activityStatusRepository;

    @InjectMocks
    ActivityUpdaterTestable activityUpdater;

    private List<ActivityStatus> createTestData() {
        ActivityStatus activityStatus1 = new ActivityStatus();
        activityStatus1.setStatus("A Status");
        activityStatus1.setActivityType(Activity.ActivityType.PLAYING);
        ActivityStatus activityStatus2 = new ActivityStatus();
        activityStatus2.setStatus("Another Status");
        activityStatus2.setActivityType(Activity.ActivityType.PLAYING);
        ActivityStatus activityStatus3 = new ActivityStatus();
        activityStatus3.setStatus("Wow More Status");
        activityStatus3.setActivityType(Activity.ActivityType.PLAYING);
        return List.of(activityStatus1, activityStatus2, activityStatus3);
    }

    @Test
    void updateNinbotActivity() {
        var testDataList = createTestData();
        when(activityStatusRepository.findAll()).thenReturn(testDataList);
        activityUpdater.updateNinbotActivity();
        var list = activityUpdater.getActivityStatusList();
        assertThat(list).hasSize(2);
        verify(shardManager).setActivity(any());
        activityUpdater.updateNinbotActivity();
        var list2 = activityUpdater.getActivityStatusList();
        assertThat(list2).hasSize(1);
    }

    @Test
    void noStatusesNoUpdate() {
        List<ActivityStatus> testDataList = new ArrayList<>();
        when(activityStatusRepository.findAll()).thenReturn(testDataList);
        activityUpdater.updateNinbotActivity();
        var list = activityUpdater.getActivityStatusList();
        assertThat(list).isEmpty();
        verifyNoInteractions(shardManager);
    }
}

class ActivityUpdaterTestable extends ActivityUpdater {
    public ActivityUpdaterTestable(ShardManager shardManager, ActivityStatusRepository activityStatusRepository) {
        super(shardManager, activityStatusRepository);
    }

    public List<ActivityStatus> getActivityStatusList() {
        return activityStatusList;
    }
}

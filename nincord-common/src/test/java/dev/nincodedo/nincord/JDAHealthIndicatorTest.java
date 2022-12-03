package dev.nincodedo.nincord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JDAHealthIndicatorTest {

    @Mock
    ShardManager shardManager;

    @InjectMocks
    JDAHealthIndicator jdaHealthIndicator;

    public static List<JDAHealthIndicatorTestData> testData() {
        JDA jda = Mockito.mock(JDA.class);
        JDA jda2 = Mockito.mock(JDA.class);
        return List.of(
                new JDAHealthIndicatorTestData(Map.of(jda, JDA.Status.CONNECTED), Status.UP),
                new JDAHealthIndicatorTestData(Map.of(jda, JDA.Status.CONNECTED, jda2, JDA.Status.DISCONNECTED),
                        Status.UP),
                new JDAHealthIndicatorTestData(Map.of(jda, JDA.Status.DISCONNECTED), Status.DOWN)
        );
    }

    @MethodSource("testData")
    @ParameterizedTest
    void multipleUp(JDAHealthIndicatorTestData testData) {
        when(shardManager.getStatuses()).thenReturn(testData.jdaStatusMap());
        var health = jdaHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(testData.status());
    }
}

record JDAHealthIndicatorTestData(Map<JDA, JDA.Status> jdaStatusMap, Status status) {
}

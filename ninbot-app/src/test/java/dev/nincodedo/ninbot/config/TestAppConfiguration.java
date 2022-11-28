package dev.nincodedo.ninbot.config;

import com.github.twitch4j.TwitchClient;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.mockito.Answers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class TestAppConfiguration {
    @MockBean
    ShardManager shardManager;
    @MockBean(answer = Answers.RETURNS_MOCKS)
    TwitchClient twitchClient;
}

package dev.nincodedo.ninbot.config;

import com.github.twitch4j.TwitchClient;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.mockito.Answers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TestConfiguration
public class TestAppConfiguration {
    @MockBean(answer = Answers.RETURNS_MOCKS)
    ShardManager shardManager;
    @MockBean(answer = Answers.RETURNS_MOCKS)
    TwitchClient twitchClient;

    @Bean
    public ExecutorService commandParserThreadPool() {
        return Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }
}

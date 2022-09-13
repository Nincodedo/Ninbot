package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.NinbotRunner;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
@MockitoSettings(strictness = Strictness.LENIENT)
class PollSchedulerTest {

    @Mock
    PollService pollService;

    @Mock
    PollAnnouncementSetup pollAnnouncementSetup;

    @Mock
    RestAction<Message> restAction;

    @InjectMocks
    PollScheduler pollScheduler;

    static List<TestParameter> channelTypes() {
        return List.of(new TestParameter(ChannelType.TEXT, TextChannel.class),
                new TestParameter(ChannelType.GUILD_PUBLIC_THREAD, ThreadChannel.class),
                new TestParameter(ChannelType.NEWS, NewsChannel.class));
    }

    @ParameterizedTest
    @MethodSource("channelTypes")
    void scheduleOne(TestParameter testParameter) {
        Poll poll = new Poll();
        poll.setChannelId("1");
        poll.setMessageId("1");
        ShardManager shardManager = Mockito.mock(ShardManager.class);
        var channel = Mockito.mock(testParameter.channelClass());

        when(shardManager.getGuildChannelById("1")).thenReturn(channel);
        when(channel.getType()).thenReturn(testParameter.channelType());
        when(channel.retrieveMessageById("1")).thenReturn(restAction);

        pollScheduler.scheduleOne(poll, shardManager);

        verify(channel, times(1)).retrieveMessageById("1");
    }

    @Test
    void scheduleOneError() {
        Poll poll = new Poll();
        poll.setChannelId("1");
        poll.setMessageId("1");
        ShardManager shardManager = Mockito.mock(ShardManager.class);
        var channel = Mockito.mock(StageChannel.class);

        when(shardManager.getGuildChannelById("1")).thenReturn(channel);
        when(channel.getType()).thenReturn(ChannelType.STAGE);

        assertThatThrownBy(() -> pollScheduler.scheduleOne(poll, shardManager))
                .isInstanceOf(IllegalStateException.class)
                .hasNoCause();
    }
}

record TestParameter(ChannelType channelType, Class<? extends GuildMessageChannel> channelClass) {
}

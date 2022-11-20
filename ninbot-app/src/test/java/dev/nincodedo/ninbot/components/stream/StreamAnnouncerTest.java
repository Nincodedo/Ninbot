package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.stats.StatManager;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class StreamAnnouncerTest {
    @Mock
    ConfigService configService;
    @Mock
    StreamMessageBuilder streamMessageBuilder;
    @Mock
    StreamingMemberRepository streamingMemberRepository;
    @Mock
    StatManager statManager;
    @Mock
    ShardManager shardManager;
    @InjectMocks
    StreamAnnouncer streamAnnouncer;

    @Test
    void announceStreamTwoStrings() {
        var streamingMember = Instancio.of(StreamingMember.class).create();
        var guild = Mockito.mock(Guild.class);
        var member = Mockito.mock(Member.class);
        when(shardManager.getGuildById(streamingMember.getGuildId())).thenReturn(guild);
        when(guild.getMemberById(streamingMember.getUserId())).thenReturn(member);
        when(guild.getId()).thenReturn(streamingMember.getGuildId());

        streamAnnouncer.announceStream(streamingMember, "Kirbo 64", "Wau");

        verify(configService).getSingleValueByName(streamingMember.getGuildId(),
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
    }

    @Test
    void announceStreamOtherOne() {
        var streamingMember = Instancio.of(StreamingMember.class).create();
        var guild = Mockito.mock(Guild.class);
        var member = Mockito.mock(Member.class);
        var activity = Mockito.mock(Activity.class);
        var richActivity = Mockito.mock(RichPresence.class);
        when(activity.isRich()).thenReturn(true);
        when(activity.asRichPresence()).thenReturn(richActivity);
        when(richActivity.getDetails()).thenReturn("Stream Title");
        when(activity.getType()).thenReturn(Activity.ActivityType.STREAMING);
        when(richActivity.getState()).thenReturn("Game Title");
        when(guild.getId()).thenReturn(streamingMember.getGuildId());

        streamAnnouncer.announceStream(streamingMember, guild, member, "https://twitch.tv/nincodedo", activity);

        verify(configService).getSingleValueByName(streamingMember.getGuildId(),
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
    }
}

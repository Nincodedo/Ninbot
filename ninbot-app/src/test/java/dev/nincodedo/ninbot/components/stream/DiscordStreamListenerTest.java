package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.NinbotApplication;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotApplication.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class DiscordStreamListenerTest {

    @Mock
    ExecutorService executorService;

    @Mock
    ComponentService componentService;

    @Mock
    StreamAnnouncer streamAnnouncer;

    @Mock
    StreamingMemberRepository streamingMemberRepository;

    @InjectMocks
    DiscordStreamListener discordStreamListener;

    @Test
    void userStartsStreamingNotAlreadyStreamingNoCooldown() {
        UserActivityStartEvent userActivityStartEvent = mock(UserActivityStartEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        Activity activity = mock(Activity.class);
        StreamingMember streamingMember = new StreamingMember();
        streamingMember.setAnnounceEnabled(true);
        var streamingUrl = "https://twitch.tv/nincodedo";

        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(streamingMemberRepository.findByUserIdAndGuildId(any(), any())).thenReturn(Optional.of(streamingMember));
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.STREAMING);
        when(activity.getUrl()).thenReturn(streamingUrl);

        discordStreamListener.onGenericUserPresence(userActivityStartEvent);

        verify(streamAnnouncer).announceStream(any(), any(), any(), any(), any());
    }

    @Test
    void userNotStreamingStartsOtherActivity() {
        UserActivityStartEvent userActivityStartEvent = mock(UserActivityStartEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        Activity activity = mock(Activity.class);
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.PLAYING);

        discordStreamListener.onGenericUserPresence(userActivityStartEvent);

        verify(streamAnnouncer, Mockito.never()).announceStream(any(), any(), any(), any(), any());
    }

    @Test
    void userStartsStreamingNotAlreadyStreamingOnCooldown() {
        UserActivityStartEvent userActivityStartEvent = mock(UserActivityStartEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        Activity activity = mock(Activity.class);
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.STREAMING);

        discordStreamListener.onGenericUserPresence(userActivityStartEvent);

        verify(streamAnnouncer, Mockito.never()).announceStream(any(), any(), any(), any(), any());
    }

    @Test
    void userStopsStreaming() {
        UserActivityEndEvent userActivityEndEvent = mock(UserActivityEndEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        User user = mock(User.class);
        when(userActivityEndEvent.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.getId()).thenReturn("123");
        when(userActivityEndEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");

        discordStreamListener.onGenericUserPresence(userActivityEndEvent);

        verify(streamAnnouncer).endStream(any(), any());
    }
}

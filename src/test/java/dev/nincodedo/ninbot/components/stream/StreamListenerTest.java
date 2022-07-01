package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
@MockitoSettings(strictness = Strictness.LENIENT)
class StreamListenerTest {

    @Mock
    ConfigService configService;

    @Mock
    ComponentService componentService;

    @Mock
    StreamingMemberRepository streamingMemberRepository;

    @InjectMocks
    StreamListener streamListener;

    @Test
    void userStartsStreamingNotAlreadyStreamingNoCooldown() {
        UserActivityStartEvent userActivityStartEvent = mock(UserActivityStartEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        Activity activity = mock(Activity.class);
        User user = mock(User.class);
        TextChannel textChannel = mock(TextChannel.class);
        MessageAction messageAction = mock(MessageAction.class);
        Role streamingRole = mock(Role.class);
        RichPresence richPresence = mock(RichPresence.class);
        AuditableRestAction auditableRestAction = mock(AuditableRestAction.class);
        Message message = mock(Message.class);

        when(configService.getValuesByName("123", ConfigConstants.STREAMING_ANNOUNCE_USERS)).thenReturn(List.of("123"));
        when(configService.getSingleValueByName("123", ConfigConstants.STREAMING_ANNOUNCE_CHANNEL)).thenReturn(Optional.of("123"));
        when(configService.getSingleValueByName("123", ConfigConstants.STREAMING_ROLE)).thenReturn(Optional.of("123"));
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(member.getId()).thenReturn("123");
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.STREAMING);
        when(userActivityStartEvent.getUser()).thenReturn(user);
        when(user.getName()).thenReturn("Nin");
        when(activity.isRich()).thenReturn(true);
        when(activity.asRichPresence()).thenReturn(richPresence);
        when(activity.getUrl()).thenReturn("https://twitch.tv/nincodedo");
        when(richPresence.getState()).thenReturn("Zeldo Breath of the Wild 2");
        when(guild.getLocale()).thenReturn(Locale.ENGLISH);
        when(guild.getMember(user)).thenReturn(member);
        when(guild.getGuildChannelById("123")).thenReturn(textChannel);
        when(textChannel.sendMessage(any(Message.class))).thenReturn(messageAction);
        when(guild.getRoleById("123")).thenReturn(streamingRole);
        when(guild.addRoleToMember(member, streamingRole)).thenReturn(auditableRestAction);
        when(textChannel.getLatestMessageId()).thenReturn("123");
        when(textChannel.retrieveMessageById("123")).thenReturn(auditableRestAction);
        when(auditableRestAction.complete()).thenReturn(message);
        when(message.getContentRaw()).thenReturn("some other stream announcement");

        streamListener.onGenericUserPresence(userActivityStartEvent);

        verify(messageAction, times(1)).queue(any());
        verify(auditableRestAction, times(1)).queue();
    }

    @Test
    void userNotStreamingStartsOtherActivity() {
        UserActivityStartEvent userActivityStartEvent = mock(UserActivityStartEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        Activity activity = mock(Activity.class);
        MessageAction messageAction = mock(MessageAction.class);
        AuditableRestAction auditableRestAction = mock(AuditableRestAction.class);
        when(configService.getValuesByName("123", ConfigConstants.STREAMING_ANNOUNCE_USERS)).thenReturn(List.of("123"));
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.PLAYING);

        streamListener.onGenericUserPresence(userActivityStartEvent);

        verify(messageAction, times(0)).queue();
        verify(auditableRestAction, times(0)).queue();
    }

    @Test
    void userStartsStreamingNotAlreadyStreamingOnCooldown() {
        UserActivityStartEvent userActivityStartEvent = mock(UserActivityStartEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        Activity activity = mock(Activity.class);
        MessageAction messageAction = mock(MessageAction.class);
        AuditableRestAction auditableRestAction = mock(AuditableRestAction.class);
        when(configService.getValuesByName("123", ConfigConstants.STREAMING_ANNOUNCE_USERS)).thenReturn(List.of("123"));
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.STREAMING);

        streamListener.onGenericUserPresence(userActivityStartEvent);

        verify(messageAction, times(0)).queue();
        verify(auditableRestAction, times(0)).queue();
    }

    @Test
    void userStopsStreaming() {
        UserActivityEndEvent userActivityEndEvent = mock(UserActivityEndEvent.class);
        Member member = mock(Member.class);
        Guild guild = mock(Guild.class);
        MessageAction messageAction = mock(MessageAction.class);
        AuditableRestAction auditableRestAction = mock(AuditableRestAction.class);
        Role streamingRole = mock(Role.class);
        User user = mock(User.class);
        when(configService.getSingleValueByName("123", ConfigConstants.STREAMING_ROLE)).thenReturn(Optional.of("123"));
        when(userActivityEndEvent.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.getId()).thenReturn("123");
        when(member.getId()).thenReturn("123");
        when(userActivityEndEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(guild.getRoleById("123")).thenReturn(streamingRole);
        when(guild.removeRoleFromMember(member, streamingRole)).thenReturn(auditableRestAction);
        when(member.getRoles()).thenReturn(List.of(streamingRole));

        streamListener.onGenericUserPresence(userActivityEndEvent);

        verify(messageAction, times(0)).queue();
        verify(auditableRestAction, times(1)).queue();
    }
}

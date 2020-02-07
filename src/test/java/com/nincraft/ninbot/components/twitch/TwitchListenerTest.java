package com.nincraft.ninbot.components.twitch;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import net.dv8tion.jda.api.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.nincraft.ninbot.NinbotRunner;
import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;

import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
@MockitoSettings(strictness = Strictness.LENIENT)
public class TwitchListenerTest {

    @Mock
    ConfigService configService;

    @Mock
    LocaleService localeService;

    @Mock
    ComponentService componentService;

    @Mock
    StreamingMemberRepository streamingMemberRepository;

    @InjectMocks
    TwitchListener twitchListener;

    @Test
    public void userStartsStreamingNotAlreadyStreamingNoCooldown() {
        UserActivityStartEvent userActivityStartEvent = Mockito.mock(UserActivityStartEvent.class);
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        Activity activity = Mockito.mock(Activity.class);
        User user = Mockito.mock(User.class);
        TextChannel textChannel = Mockito.mock(TextChannel.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        Role streamingRole = Mockito.mock(Role.class);
        RichPresence richPresence = Mockito.mock(RichPresence.class);
        AuditableRestAction auditableRestAction = Mockito.mock(AuditableRestAction.class);
        when(configService.getValuesByName("123", ConfigConstants.STREAMING_ANNOUNCE_USERS)).thenReturn(Arrays.asList("123"));
        when(configService.getSingleValueByName("123", ConfigConstants.STREAMING_ANNOUNCE_CHANNEL)).thenReturn(Optional.of("123"));
        when(configService.getSingleValueByName("123", ConfigConstants.STREAMING_ROLE)).thenReturn(Optional.of("123"));
        when(userActivityStartEvent.getMember()).thenReturn(member);
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
        when(localeService.getLocale("123")).thenReturn(new Locale("en"));
        when(guild.getMember(user)).thenReturn(member);
        when(guild.getTextChannelById("123")).thenReturn(textChannel);
        when(textChannel.sendMessage(Mockito.any(Message.class))).thenReturn(messageAction);
        when(guild.getRoleById("123")).thenReturn(streamingRole);
        when(guild.addRoleToMember(member, streamingRole)).thenReturn(auditableRestAction);
        twitchListener.onGenericUserPresence(userActivityStartEvent);
        Mockito.verify(messageAction, times(1)).queue();
        Mockito.verify(auditableRestAction, times(1)).queue();
    }

    @Test
    public void userNotStreamingStartsOtherActivity() {
        UserActivityStartEvent userActivityStartEvent = Mockito.mock(UserActivityStartEvent.class);
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        Activity activity = Mockito.mock(Activity.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        AuditableRestAction auditableRestAction = Mockito.mock(AuditableRestAction.class);
        when(configService.getValuesByName("123", ConfigConstants.STREAMING_ANNOUNCE_USERS)).thenReturn(Arrays.asList("123"));
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.DEFAULT);
        twitchListener.onGenericUserPresence(userActivityStartEvent);
        Mockito.verify(messageAction, times(0)).queue();
        Mockito.verify(auditableRestAction, times(0)).queue();
    }

    @Test
    public void userStartsStreamingNotAlreadyStreamingOnCooldown() {
        UserActivityStartEvent userActivityStartEvent = Mockito.mock(UserActivityStartEvent.class);
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        Activity activity = Mockito.mock(Activity.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        AuditableRestAction auditableRestAction = Mockito.mock(AuditableRestAction.class);
        when(configService.getValuesByName("123", ConfigConstants.STREAMING_ANNOUNCE_USERS)).thenReturn(Arrays.asList("123"));
        when(userActivityStartEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(userActivityStartEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(userActivityStartEvent.getNewActivity()).thenReturn(activity);
        when(activity.getType()).thenReturn(Activity.ActivityType.STREAMING);
        twitchListener.onGenericUserPresence(userActivityStartEvent);
        Mockito.verify(messageAction, times(0)).queue();
        Mockito.verify(auditableRestAction, times(0)).queue();
    }

    @Test
    public void userStopsStreaming() {
        UserActivityEndEvent userActivityEndEvent = Mockito.mock(UserActivityEndEvent.class);
        Member member = Mockito.mock(Member.class);
        Guild guild = Mockito.mock(Guild.class);
        MessageAction messageAction = Mockito.mock(MessageAction.class);
        AuditableRestAction auditableRestAction = Mockito.mock(AuditableRestAction.class);
        Role streamingRole = Mockito.mock(Role.class);
        when(configService.getSingleValueByName("123", ConfigConstants.STREAMING_ROLE)).thenReturn(Optional.of("123"));
        when(userActivityEndEvent.getMember()).thenReturn(member);
        when(member.getId()).thenReturn("123");
        when(userActivityEndEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("123");
        when(guild.getRoleById("123")).thenReturn(streamingRole);
        when(guild.removeRoleFromMember(member, streamingRole)).thenReturn(auditableRestAction);
        twitchListener.onGenericUserPresence(userActivityEndEvent);
        Mockito.verify(messageAction, times(0)).queue();
        Mockito.verify(auditableRestAction, times(1)).queue();
    }
}

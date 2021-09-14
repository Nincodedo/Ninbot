package dev.nincodedo.ninbot.components.channel;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class TempVoiceChannelManagerTest {

    @Mock
    TempVoiceChannelRepository tempVoiceChannelRepository;

    @Mock
    ComponentService componentService;

    @Mock
    StatManager statManager;

    @InjectMocks
    TempVoiceChannelManager tempVoiceChannelManager;

    @Test
    void onGuildVoiceJoin() {
        var guild = Mockito.mock(Guild.class);
        var joinEvent = Mockito.mock(GuildVoiceJoinEvent.class);
        var jda = Mockito.mock(JDA.class);
        var selfMember = Mockito.mock(Member.class);
        var member = Mockito.mock(Member.class);
        var voiceChannelJoined = Mockito.mock(VoiceChannel.class);
        var restAction = Mockito.mock(ChannelAction.class);

        when(joinEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(joinEvent.getChannelJoined()).thenReturn(voiceChannelJoined);
        when(joinEvent.getMember()).thenReturn(member);
        when(voiceChannelJoined.getName()).thenReturn(Emojis.PLUS + " wot");
        when(selfMember.hasPermission(any(Permission.class))).thenReturn(true);
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        when(member.getId()).thenReturn("1");
        when(voiceChannelJoined.createCopy()).thenReturn(restAction);
        when(restAction.setName(anyString())).thenReturn(restAction);
        when(restAction.addPermissionOverride(member, Arrays.asList(Permission.VOICE_MOVE_OTHERS,
                Permission.PRIORITY_SPEAKER, Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS,
                Permission.VOICE_DEAF_OTHERS), null)).thenReturn(restAction);
        when(guild.createVoiceChannel(anyString())).thenReturn(restAction);

        tempVoiceChannelManager.onGuildVoiceJoin(joinEvent);

        verify(tempVoiceChannelRepository, times(1)).save(new TempVoiceChannel("1", "1"));
    }
}
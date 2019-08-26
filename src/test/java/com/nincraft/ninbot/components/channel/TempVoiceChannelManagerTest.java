package com.nincraft.ninbot.components.channel;

import com.nincraft.ninbot.NinbotTest;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.config.component.ComponentService;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TempVoiceChannelManagerTest extends NinbotTest {

    @Mock
    TempVoiceChannelRepository tempVoiceChannelRepository;

    @Mock
    ComponentService componentService;

    @InjectMocks
    TempVoiceChannelManager tempVoiceChannelManager;

    @Test
    public void onGuildVoiceJoin() {
        val guild = Mockito.mock(Guild.class);
        val joinEvent = Mockito.mock(GuildVoiceJoinEvent.class);
        val jda = Mockito.mock(JDA.class);
        val selfUser = Mockito.mock(SelfUser.class);
        val member = Mockito.mock(Member.class);
        val voiceChannelJoined = Mockito.mock(VoiceChannel.class);
        val restAction = Mockito.mock(ChannelAction.class);

        when(joinEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(guild.getMember(selfUser)).thenReturn(member);
        when(joinEvent.getChannelJoined()).thenReturn(voiceChannelJoined);
        when(joinEvent.getMember()).thenReturn(member);
        when(voiceChannelJoined.getName()).thenReturn(Emojis.PLUS + " wot");
        when(member.hasPermission(any(Permission.class))).thenReturn(true);
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        when(guild.createVoiceChannel(anyString())).thenReturn(restAction);

        tempVoiceChannelManager.onGuildVoiceJoin(joinEvent);

        verify(guild, times(1)).createVoiceChannel("Nincodedo's wot");
    }
}
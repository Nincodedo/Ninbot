package dev.nincodedo.ninbot.components.channel.voice;

import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.stats.StatManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TempVoiceChannelManagerTest {

    @Mock
    TempVoiceChannelRepository tempVoiceChannelRepository;

    @Mock
    ComponentService componentService;

    @Mock
    StatManager statManager;

    @Mock
    ExecutorService executorService;

    @InjectMocks
    TempVoiceChannelManager tempVoiceChannelManager;

    @Captor
    private ArgumentCaptor<Consumer> lambdaCaptor;

    @Test
    void onGuildVoiceJoin() {
        var guild = Mockito.mock(Guild.class);
        var joinEvent = Mockito.mock(GuildVoiceUpdateEvent.class);
        var selfMember = Mockito.mock(Member.class);
        var member = Mockito.mock(Member.class);
        var voiceChannelJoined = Mockito.mock(AudioChannelUnion.class);
        var restAction = Mockito.mock(ChannelAction.class);
        var lastRestAction = Mockito.mock(ChannelAction.class);
        var moveVoiceAction = Mockito.mock(RestAction.class);
        var permissionContainer = Mockito.mock(IPermissionContainer.class);
        var permissionContainerManager = Mockito.mock(IPermissionContainerManager.class);
        var voiceChannel = Mockito.mock(VoiceChannel.class);

        when(joinEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(joinEvent.getChannelJoined()).thenReturn(voiceChannelJoined);
        when(voiceChannelJoined.asVoiceChannel()).thenReturn(voiceChannel);
        when(joinEvent.getMember()).thenReturn(member);
        when(voiceChannelJoined.getName()).thenReturn(Emojis.PLUS + " wot");
        when(selfMember.hasPermission(any(Permission.class))).thenReturn(true);
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        when(member.getId()).thenReturn("1");
        when(member.getIdLong()).thenReturn(1L);
        when(voiceChannel.createCopy()).thenReturn(restAction);
        when(restAction.setName(anyString())).thenReturn(lastRestAction);
        when(guild.moveVoiceMember(member, voiceChannel)).thenReturn(moveVoiceAction);
        when(voiceChannelJoined.getType()).thenReturn(ChannelType.VOICE);
        when(voiceChannel.getPermissionContainer()).thenReturn(permissionContainer);
        when(permissionContainer.getManager()).thenReturn(permissionContainerManager);
        when(permissionContainerManager.putMemberPermissionOverride(member.getIdLong(),
                Arrays.asList(Permission.VOICE_MOVE_OTHERS,
                        Permission.PRIORITY_SPEAKER, Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS,
                        Permission.VOICE_DEAF_OTHERS), null)).thenReturn(permissionContainerManager);

        tempVoiceChannelManager.onGuildVoiceUpdate(joinEvent);

        verify(lastRestAction).queue(lambdaCaptor.capture());

        Consumer<VoiceChannel> consumer = lambdaCaptor.getValue();
        consumer.accept(voiceChannel);

        verify(tempVoiceChannelRepository).save(new TempVoiceChannel(member.getId(), voiceChannelJoined.getId()));
    }
}

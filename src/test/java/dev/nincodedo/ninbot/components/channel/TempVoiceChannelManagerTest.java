package dev.nincodedo.ninbot.components.channel;

import dev.nincodedo.ninbot.NinbotRunner;
import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.logging.ServerLogger;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionContainer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    ServerLogger serverLogger;

    @InjectMocks
    TempVoiceChannelManager tempVoiceChannelManager;

    @Captor
    private ArgumentCaptor<Consumer> lambdaCaptor;

    @Test
    void onGuildVoiceJoin() {
        var guild = Mockito.mock(Guild.class);
        var joinEvent = Mockito.mock(GuildVoiceJoinEvent.class);
        var selfMember = Mockito.mock(Member.class);
        var member = Mockito.mock(Member.class);
        var voiceChannelJoined = Mockito.mock(VoiceChannel.class);
        var restAction = Mockito.mock(ChannelAction.class);
        var lastRestAction = Mockito.mock(ChannelAction.class);
        var moveVoiceAction = Mockito.mock(RestAction.class);
        var permissionContainer = Mockito.mock(IPermissionContainer.class);
        var permissionContainerManager = Mockito.mock(IPermissionContainerManager.class);

        when(joinEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("1");
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(joinEvent.getChannelJoined()).thenReturn(voiceChannelJoined);
        when(joinEvent.getMember()).thenReturn(member);
        when(voiceChannelJoined.getName()).thenReturn(Emojis.PLUS + " wot");
        when(selfMember.hasPermission(any(Permission.class))).thenReturn(true);
        when(member.getEffectiveName()).thenReturn("Nincodedo");
        when(member.getId()).thenReturn("1");
        when(member.getIdLong()).thenReturn(1L);
        when(voiceChannelJoined.createCopy()).thenReturn(restAction);
        when(restAction.setName(anyString())).thenReturn(lastRestAction);
        when(guild.moveVoiceMember(member, voiceChannelJoined)).thenReturn(moveVoiceAction);
        when(voiceChannelJoined.getType()).thenReturn(ChannelType.VOICE);
        when(voiceChannelJoined.getPermissionContainer()).thenReturn(permissionContainer);
        when(permissionContainer.getManager()).thenReturn(permissionContainerManager);
        when(permissionContainerManager.putMemberPermissionOverride(member.getIdLong(),
                Arrays.asList(Permission.VOICE_MOVE_OTHERS,
                        Permission.PRIORITY_SPEAKER, Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS,
                        Permission.VOICE_DEAF_OTHERS), null)).thenReturn(permissionContainerManager);

        tempVoiceChannelManager.onGuildVoiceJoin(joinEvent);

        verify(lastRestAction).queue(lambdaCaptor.capture());

        Consumer<VoiceChannel> consumer = lambdaCaptor.getValue();
        consumer.accept(voiceChannelJoined);

        verify(tempVoiceChannelRepository).save(new TempVoiceChannel(member.getId(), voiceChannelJoined.getId()));
    }
}

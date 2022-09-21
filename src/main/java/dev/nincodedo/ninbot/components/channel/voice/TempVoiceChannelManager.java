package dev.nincodedo.ninbot.components.channel.voice;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentType;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@Slf4j
public class TempVoiceChannelManager extends StatAwareListenerAdapter {

    private TempVoiceChannelRepository repository;
    private ComponentService componentService;
    private String componentName;

    public TempVoiceChannelManager(TempVoiceChannelRepository tempVoiceChannelRepository,
            ComponentService componentService, StatManager statManager) {
        super(statManager);
        this.repository = tempVoiceChannelRepository;
        this.componentService = componentService;
        this.componentName = "voice-channel-manager";
        componentService.registerComponent(componentName, ComponentType.ACTION);
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        checkIfShouldCreateTempChannel(event.getGuild(), event.getMember(), event.getChannelJoined());
    }

    private void checkIfShouldCreateTempChannel(Guild guild, Member member, AudioChannelUnion channelJoined) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        log.trace("hasPermission: {}, channel join {} is temp creator: {}", hasManageChannelPermission(guild
        ), channelJoined
                .getName(), channelJoined.getName()
                .startsWith(Emojis.PLUS));
        if (hasManageChannelPermission(guild) && channelJoined
                .getName()
                .startsWith(Emojis.PLUS)) {
            countOneStat(componentName, guild.getId());
            createTemporaryChannel(channelJoined, guild, member);
        }
    }

    private void createTemporaryChannel(AudioChannelUnion channelJoined, Guild guild, Member member) {
        var channelNameType = channelJoined.getName().substring(2);
        var channelName = String.format("%s's %s", member.getEffectiveName().replace(Emojis.PLUS, ""), channelNameType);
        log.trace("Creating temporary channel named {} for member {} in server {}", channelName,
                FormatLogObject.memberInfo(member),
                FormatLogObject.guildName(guild));
        if (channelJoined.getType() == ChannelType.VOICE) {
            var voiceChannel = (VoiceChannel) channelJoined;
            voiceChannel.createCopy()
                    .setName(channelName)
                    .queue(saveAndMove(guild, member));
        }
    }

    Consumer<VoiceChannel> saveAndMove(Guild guild, Member member) {
        return voiceChannel -> {
            repository.save(new TempVoiceChannel(member.getId(), voiceChannel.getId()));
            guild.moveVoiceMember(member, voiceChannel).queue();
            voiceChannel.getPermissionContainer()
                    .getManager()
                    .putMemberPermissionOverride(member.getIdLong(), Arrays.asList(Permission.VOICE_MOVE_OTHERS,
                            Permission.PRIORITY_SPEAKER, Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS,
                            Permission.VOICE_DEAF_OTHERS), null)
                    .queue();
        };
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        checkIfShouldCreateTempChannel(event.getGuild(), event.getMember(), event.getChannelJoined());
        checkIfShouldDeleteTempChannel(event.getGuild(), event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        checkIfShouldDeleteTempChannel(event.getGuild(), event.getChannelLeft());
    }

    private void checkIfShouldDeleteTempChannel(Guild guild, AudioChannelUnion audioChannel) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        if (isTemporaryChannel(audioChannel.getId()) && hasManageChannelPermission(guild) && audioChannel.getMembers()
                .isEmpty()) {
            deleteTemporaryChannel(audioChannel);
        }
    }

    private boolean isTemporaryChannel(String voiceChannelId) {
        return repository.findByVoiceChannelId(voiceChannelId).isPresent();
    }

    private void deleteTemporaryChannel(AudioChannelUnion audioChannel) {
        audioChannel.delete()
                .queueAfter(10, TimeUnit.SECONDS, success ->
                        repository.findByVoiceChannelId(audioChannel.getId())
                                .ifPresent(tempVoiceChannel -> repository.delete(tempVoiceChannel)));
    }

    private boolean hasManageChannelPermission(Guild guild) {
        return guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL);
    }
}

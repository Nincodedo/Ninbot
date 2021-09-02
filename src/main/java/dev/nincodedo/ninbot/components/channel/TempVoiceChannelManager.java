package dev.nincodedo.ninbot.components.channel;

import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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

    private void checkIfShouldCreateTempChannel(Guild guild, Member member, VoiceChannel channelJoined) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        log.trace("hasPermission: {}, channel join {} is temp creator: {}", hasPermission(guild,
                Permission.MANAGE_CHANNEL), channelJoined
                .getName(), channelJoined.getName()
                .startsWith(Emojis.PLUS));
        if (hasPermission(guild, Permission.MANAGE_CHANNEL) && channelJoined
                .getName()
                .startsWith(Emojis.PLUS)) {
            countOneStat(componentName, guild.getId());
            createTemporaryChannel(channelJoined, guild, member);
        }
    }

    private void createTemporaryChannel(VoiceChannel channelJoined, Guild guild, Member member) {
        var channelNameType = channelJoined.getName().substring(2);
        var channelName = String.format("%s's %s", member.getEffectiveName().replace(Emojis.PLUS, ""), channelNameType);
        log.trace("Creating temporary channel named {} for member id {} in server id {}", channelName, member.getId(),
                guild.getId());

        channelJoined.createCopy()
                .setName(channelName)
                .addPermissionOverride(member, Arrays.asList(Permission.VOICE_MOVE_OTHERS,
                        Permission.PRIORITY_SPEAKER, Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS,
                        Permission.VOICE_DEAF_OTHERS), null)
                .queue(voiceChannel -> {
                    repository.save(new TempVoiceChannel(member.getId(), voiceChannel.getId()));
                    guild.moveVoiceMember(member, voiceChannel).queue();
                });
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

    private void checkIfShouldDeleteTempChannel(Guild guild, VoiceChannel voiceChannel) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        if (isTemporaryChannel(voiceChannel.getId()) && hasPermission(guild, Permission.MANAGE_CHANNEL)
                && voiceChannel.getMembers()
                .isEmpty()) {
            deleteTemporaryChannel(voiceChannel);
        }
    }

    private boolean isTemporaryChannel(String voiceChannelId) {
        return repository.findByVoiceChannelId(voiceChannelId).isPresent();
    }

    private void deleteTemporaryChannel(VoiceChannel voiceChannel) {
        voiceChannel.delete()
                .queueAfter(10, TimeUnit.SECONDS, success ->
                        repository.findByVoiceChannelId(voiceChannel.getId())
                                .ifPresent(tempVoiceChannel -> repository.delete(tempVoiceChannel)));
    }

    private boolean hasPermission(Guild guild, Permission permission) {
        return guild.getSelfMember().hasPermission(permission);
    }
}

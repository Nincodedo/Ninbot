package com.nincraft.ninbot.components.channel;

import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.config.component.ComponentService;
import com.nincraft.ninbot.components.config.component.ComponentType;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.OrderAction;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class TempVoiceChannelManager extends ListenerAdapter {

    private TempVoiceChannelRepository repository;
    private ComponentService componentService;
    private String componentName;

    public TempVoiceChannelManager(TempVoiceChannelRepository tempVoiceChannelRepository,
            ComponentService componentService) {
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
        log.trace("hasPermission: {}", hasPermission(guild, Permission.MANAGE_CHANNEL));
        log.trace("channel join {} is temp creator: {}", channelJoined
                .getName(), channelJoined.getName()
                .startsWith(Emojis.PLUS));
        if (hasPermission(guild, Permission.MANAGE_CHANNEL) && channelJoined
                .getName()
                .startsWith(Emojis.PLUS)) {
            createTemporaryChannel(channelJoined, guild, member);
        }
    }

    private void createTemporaryChannel(VoiceChannel channelJoined, Guild guild, Member user) {
        val channelNameType = channelJoined.getName().substring(2);
        val channelName = String.format("%s's %s", user.getEffectiveName().replace(Emojis.PLUS, ""), channelNameType);
        log.info("Creating temporary channel named {} for {} in server {}", channelName, user.getEffectiveName(),
                guild.getId());
        createVoiceChannel(guild, channelJoined, channelName)
                .queue(voiceChannel -> {
                    TempVoiceChannel channel = new TempVoiceChannel(user.getId(), voiceChannel.getId());
                    repository.save(channel);
                    guild.moveVoiceMember(user, voiceChannel).queue(aVoid -> {
                        val position = channelJoined.getPosition();
                        modifyVoiceChannelPositions(guild, channelJoined)
                                .selectPosition(voiceChannel)
                                .moveTo(position + 1)
                                .queue(aVoid1 -> voiceChannel.createPermissionOverride(user)
                                        .setAllow(Arrays.asList(Permission.VOICE_MOVE_OTHERS,
                                                Permission.PRIORITY_SPEAKER,
                                                Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS,
                                                Permission.VOICE_DEAF_OTHERS))
                                        .queue());
                    });
                });
    }

    private RestAction<VoiceChannel> createVoiceChannel(Guild guild, VoiceChannel joinedChannel,
            String format) {
        if (joinedChannel.getParent() != null) {
            return joinedChannel.getParent().createVoiceChannel(format);
        } else {
            return guild.createVoiceChannel(format);
        }
    }

    private OrderAction<GuildChannel, ChannelOrderAction> modifyVoiceChannelPositions(
            Guild guild, VoiceChannel joinedChannel) {
        if (joinedChannel.getParent() != null) {
            return joinedChannel.getParent().modifyVoiceChannelPositions();
        } else {
            return guild.modifyVoiceChannelPositions();
        }
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
                .queueAfter(10, TimeUnit.SECONDS, avoid ->
                        repository.findByVoiceChannelId(voiceChannel.getId())
                                .ifPresent(tempVoiceChannel2 -> repository.delete(tempVoiceChannel2)));
    }

    private boolean hasPermission(Guild guild, Permission permission) {
        return guild.getMember(guild.getJDA().getSelfUser()).hasPermission(permission);
    }
}

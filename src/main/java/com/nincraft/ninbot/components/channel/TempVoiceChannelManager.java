package com.nincraft.ninbot.components.channel;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.requests.restaction.order.OrderAction;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Log4j2
public class TempVoiceChannelManager extends ListenerAdapter {

    private TempVoiceChannelRepository repository;

    public TempVoiceChannelManager(TempVoiceChannelRepository tempVoiceChannelRepository) {
        this.repository = tempVoiceChannelRepository;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        val guild = event.getGuild();
        val user = event.getMember();
        log.trace("onGuildVoiceJoin - hasPermission: {}", hasPermission(guild, Permission.MANAGE_CHANNEL));
        log.trace("onGuildVoiceJoin - channel join {} is temp creator: {}", event.getChannelJoined().getName(), event.getChannelJoined()
                .getName()
                .startsWith("➕"));
        if (hasPermission(guild, Permission.MANAGE_CHANNEL) && event.getChannelJoined()
                .getName()
                .startsWith("➕")) {
            createTemporaryChannel(event, guild, user);
        }
    }

    private void createTemporaryChannel(GuildVoiceJoinEvent event, Guild guild, Member user) {
        val joinedChannel = event.getChannelJoined();
        val channelNameType = joinedChannel.getName().substring(2);
        val channelName = String.format("%s's %s", user.getEffectiveName(), channelNameType);
        log.info("Creating temporary channel named {} for {} in server {}", channelName, user.getEffectiveName(), guild.getId());
        createVoiceChannel(guild, joinedChannel, channelName)
                .queue(voiceChannel -> {
                    guild.moveVoiceMember(user, voiceChannel).queue();
                    val position = event.getChannelJoined().getPosition();
                    modifyVoiceChannelPositions(guild, joinedChannel)
                            .selectPosition(voiceChannel)
                            .moveTo(position + 1)
                            .queue();
                    voiceChannel.createPermissionOverride(user)
                            .setAllow(Arrays.asList(Permission.VOICE_MOVE_OTHERS, Permission.PRIORITY_SPEAKER,
                                    Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
                            .queue();
                    TempVoiceChannel channel = new TempVoiceChannel(user.getId(), voiceChannel.getId());
                    repository.save(channel);
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
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        val guild = event.getGuild();
        val voiceChannel = event.getChannelLeft();
        if (isTemporaryChannel(voiceChannel.getId()) && hasPermission(guild, Permission.MANAGE_CHANNEL)
                && voiceChannel.getMembers()
                .isEmpty()) {
            deleteTemporaryChannel(event, voiceChannel);
        }
    }

    private boolean isTemporaryChannel(String voiceChannelId) {
        return repository.findByVoiceChannelId(voiceChannelId).isPresent();
    }

    private void deleteTemporaryChannel(GuildVoiceLeaveEvent event, VoiceChannel voiceChannel) {
        voiceChannel.delete()
                .queue(
                        avoid ->
                                repository.findByVoiceChannelId(event.getChannelLeft().getId())
                                        .ifPresent(tempVoiceChannel2 -> repository.delete(tempVoiceChannel2))
                );
    }

    private boolean hasPermission(Guild guild, Permission permission) {
        return guild.getMember(guild.getJDA().getSelfUser()).hasPermission(permission);
    }
}

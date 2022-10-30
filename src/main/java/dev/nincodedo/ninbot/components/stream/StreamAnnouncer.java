package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class StreamAnnouncer {

    private ConfigService configService;
    private StreamMessageBuilder streamMessageBuilder;
    private StreamingMemberRepository streamingMemberRepository;
    private StatManager statManager;
    private ShardManager shardManager;
    private String componentName;

    public StreamAnnouncer(ConfigService configService, StreamMessageBuilder streamMessageBuilder,
            StreamingMemberRepository streamingMemberRepository, StatManager statManager,
            @Lazy ShardManager shardManager) {
        this.configService = configService;
        this.streamMessageBuilder = streamMessageBuilder;
        this.streamingMemberRepository = streamingMemberRepository;
        this.statManager = statManager;
        this.shardManager = shardManager;
        this.componentName = "stream-announce";
    }

    public void announceStream(StreamingMember streamingMember, String gameName, String streamTitle) {
        var guild = shardManager.getGuildById(streamingMember.getGuildId());
        if (guild == null) {
            log.error("Failed to get guild {} for stream member {}", streamingMember.getGuildId(), streamingMember);
            return;
        }
        var member = guild.getMemberById(streamingMember.getUserId());
        var streamingUrl = "https://twitch.tv/" + streamingMember.getTwitchUsername();
        announceStream(streamingMember, guild, member, streamingUrl, gameName, streamTitle);
    }

    public void announceStream(StreamingMember streamingMember, Guild guild, Member member, String streamingUrl,
            Activity activity) {
        String gameName = getGameName(activity);
        String streamTitle = getStreamTitle(activity);
        announceStream(streamingMember, guild, member, streamingUrl, gameName, streamTitle);
    }

    public void announceStream(StreamingMember streamingMember, Guild guild, Member member, String streamingUrl,
            String gameName, String streamTitle) {
        var guildId = guild.getId();
        var streamingAnnounceChannel = configService.getSingleValueByName(guildId,
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            var guildChannel = guild.getGuildChannelById(streamingAnnounceChannelString);
            if (streamingUrl != null && guildChannel instanceof GuildMessageChannelUnion channelUnion) {
                var channel = channelUnion.asStandardGuildMessageChannel();
                channel.sendMessage(streamMessageBuilder.buildStreamAnnounceMessage(member, streamingUrl,
                                gameName, streamTitle, guild))
                        .queue(message -> {
                            statManager.addOneCount(componentName, StatCategory.LISTENER, guild.getId());
                            updateStreamMemberWithMessageId(streamingMember, message.getId());
                            addRole(guild, member);
                        });
                log.trace("Queued stream message for {} to channel {}", FormatLogObject.memberInfo(member),
                        FormatLogObject.channelInfo(channel));
            } else {
                log.trace("Announcement channel or streaming URL was null, not announcing stream for {} on server {}"
                        , FormatLogObject.memberInfo(member), FormatLogObject.guildName(guild));
            }
        });
    }

    private String getStreamTitle(Activity activity) {
        String streamTitle = null;
        if (activity != null && activity.isRich()) {
            var richActivity = activity.asRichPresence();
            if (richActivity != null) {
                streamTitle = richActivity.getDetails();
            }
        }
        return streamTitle;
    }

    private String getGameName(Activity activity) {
        String gameName = null;
        if (activity != null) {
            if (activity.isRich()) {
                var richActivity = activity.asRichPresence();
                if (richActivity != null) {
                    gameName = richActivity.getState();
                }
            } else {
                gameName = activity.getName();
            }
        }
        return gameName;
    }

    private void updateStreamMemberWithMessageId(StreamingMember streamingMember, String messageId) {
        streamingMemberRepository.findByUserIdAndGuildId(streamingMember.getUserId(), streamingMember.getGuildId())
                .flatMap(StreamingMember::currentStream)
                .ifPresent(streamInstance -> {
                    streamInstance.setAnnounceMessageId(messageId);
                    streamingMember.updateCurrentStream(streamInstance);
                    streamingMemberRepository.save(streamingMember);
                });
    }

    private void removeRole(Guild guild, Member member) {
        configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE).ifPresent(roleId -> {
            var streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null && member.getRoles().contains(streamingRole)) {
                guild.removeRoleFromMember(member, streamingRole).queue();
            }
        });
    }

    private void addRole(Guild guild, Member member) {
        var streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            var streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                log.trace("Adding role {} to {}", FormatLogObject.roleInfo(streamingRole),
                        FormatLogObject.memberInfo(member));
                guild.addRoleToMember(member, streamingRole).queue();
            } else {
                log.error("Could not add role ID {} for {}", streamingRoleId, FormatLogObject.memberInfo(member));
            }
        });
    }

    public void endStream(Guild guild, Member member) {
        removeRole(guild, member);
    }

    public void endStream(StreamingMember streamingMember) {
        var guild = shardManager.getGuildById(streamingMember.getGuildId());
        if (guild == null) {
            log.error("Failed to get guild {} for stream member {}", streamingMember.getGuildId(), streamingMember);
            return;
        }
        var member = guild.getMemberById(streamingMember.getUserId());
        endStream(guild, member);
    }
}

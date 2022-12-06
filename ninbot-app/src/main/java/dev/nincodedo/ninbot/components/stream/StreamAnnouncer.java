package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.stats.StatCategory;
import dev.nincodedo.nincord.stats.StatManager;
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

    public void announceStream(StreamingMember streamingMember) {
        var guild = shardManager.getGuildById(streamingMember.getGuildId());
        if (guild == null) {
            log.error("Failed to get guild {} for stream member {}", streamingMember.getGuildId(), streamingMember);
            return;
        }
        var member = guild.getMemberById(streamingMember.getUserId());
        var streamingUrl = "https://twitch.tv/" + streamingMember.getTwitchUsername();
        streamingMember.currentStream().ifPresent(streamInstance -> streamInstance.setUrl(streamingUrl));
        streamingMemberRepository.save(streamingMember);
        announceStream(streamingMember, guild, member);
    }

    public void announceStream(StreamingMember streamingMember, Guild guild, Member member, String streamingUrl,
            Activity activity) {
        streamingMember.currentStream().ifPresent(streamInstance -> {
            streamInstance.setGame(getGameName(activity));
            streamInstance.setTitle(getStreamTitle(activity));
            streamInstance.setUrl(streamingUrl);
        });
        streamingMemberRepository.save(streamingMember);
        announceStream(streamingMember, guild, member);
    }

    public void announceStream(StreamingMember streamingMember, Guild guild, Member member) {
        var guildId = guild.getId();
        var streamingAnnounceChannel = configService.getSingleValueByName(guildId,
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            var guildChannel = guild.getGuildChannelById(streamingAnnounceChannelString);
            var streamInstanceOptional = streamingMember.currentStream();
            if (streamInstanceOptional.isPresent() && streamInstanceOptional.get().getUrl() != null
                    && guildChannel instanceof GuildMessageChannelUnion channelUnion) {
                var channel = channelUnion.asStandardGuildMessageChannel();
                channel.sendMessage(streamMessageBuilder.buildStreamAnnounceMessage(member,
                                streamInstanceOptional.get(), guild))
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
            var richPresence = activity.asRichPresence();
            if (activity.getType() == Activity.ActivityType.PLAYING) {
                gameName = activity.getName();
            } else if (activity.getType() == Activity.ActivityType.STREAMING && richPresence != null) {
                gameName = richPresence.getState();
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
        var streamingRoleIdOptional = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleIdOptional.ifPresent(roleId -> {
            var streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                log.trace("Adding role {} to {}", FormatLogObject.roleInfo(streamingRole),
                        FormatLogObject.memberInfo(member));
                guild.addRoleToMember(member, streamingRole).queue();
            } else {
                log.error("Could not add role ID {} for {}", roleId, FormatLogObject.memberInfo(member));
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

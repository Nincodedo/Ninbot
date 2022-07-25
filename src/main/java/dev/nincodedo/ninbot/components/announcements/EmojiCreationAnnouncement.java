package dev.nincodedo.ninbot.components.announcements;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Slf4j
@Component
public class EmojiCreationAnnouncement extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public EmojiCreationAnnouncement(StatManager statManager, ConfigService configService,
            ComponentService componentService) {
        super(statManager);
        this.componentService = componentService;
        this.configService = configService;
        componentName = "emote-added-announcement";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onEmojiAdded(@NotNull EmojiAddedEvent event) {
        if (componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        log.trace("Event Response {}: Running EmoteCreationAnnouncement for server {}", event.getResponseNumber(),
                FormatLogObject.guildName(event.getGuild()));
        var optionalChannelId = configService.getSingleValueByName(event.getGuild()
                .getId(), ConfigConstants.EMOTE_ADDED_ANNOUNCEMENT_CHANNEL_ID);
        optionalChannelId.ifPresent(channelId -> {
            log.trace("Event Response {}: Emote announcement channel id {}", event.getResponseNumber(),
                    channelId);
            var channel = (GuildMessageChannel) event.getGuild().getGuildChannelById(channelId);
            if (channel == null) {
                log.trace("Event Response {}: Channel id {} not found", event.getResponseNumber(), channelId);
                return;
            }
            log.trace("Event Response {}: Found channel {}", event.getResponseNumber(),
                    FormatLogObject.channelInfo(channel));
            var emoji = event.getEmoji();
            countOneStat(componentName, event.getGuild().getId());
            Member member = getMemberFromAudit(event, channel);
            log.trace("Event Response {}: Sending message for {} in {}", event.getResponseNumber(), emoji.getName(),
                    FormatLogObject.guildName(event.getGuild()));
            channel.sendMessage(buildAnnouncementMessage(emoji, event.getGuild(), member))
                    .queue(message -> {
                        log.trace("Event Response {}: Sent message, adding reaction for {} in {}",
                                event.getResponseNumber(),
                                emoji.getName(), FormatLogObject.guildName(event.getGuild()));
                        message.addReaction(emoji).queue();
                    }, throwable -> log.trace("Event Response {}: Failed to send message for {} in {}",
                            event.getResponseNumber(), emoji.getName(), FormatLogObject.guildName(event.getGuild())));
        });
    }

    @Nullable
    private Member getMemberFromAudit(EmojiAddedEvent event, GuildChannel channel) {
        var selfMember = event.getGuild().getMember(event.getJDA().getSelfUser());
        var recentAuditUser = event.getGuild().retrieveAuditLogs().complete().get(0).getUser();
        if (selfMember == null || recentAuditUser == null) {
            return null;
        }
        if (selfMember.getPermissions(channel).contains(Permission.VIEW_AUDIT_LOGS)) {
            return event.getGuild().getMember(recentAuditUser);
        } else {
            return null;
        }
    }

    @NotNull
    private Message buildAnnouncementMessage(Emoji emoji, Guild guild, Member member) {
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append(resourceBundle.getString("listener.emote.announce.message"));
        if (member != null) {
            messageBuilder.append(" ");
            messageBuilder.append(resourceBundle.getString("listener.emote.announce.message.member"));
            messageBuilder.append(member);
        }
        messageBuilder.append("\n");
        messageBuilder.append(emoji.getFormatted());
        return messageBuilder.build();
    }
}

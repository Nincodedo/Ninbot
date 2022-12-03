package dev.nincodedo.ninbot.components.announcements;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.ComponentType;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class EmojiCreationAnnouncement extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public EmojiCreationAnnouncement(StatManager statManager,
            @Qualifier("statCounterThreadPool") ExecutorService executorService, ConfigService configService,
            ComponentService componentService) {
        super(statManager, executorService);
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
        if (optionalChannelId.isEmpty()) {
            return;
        }
        var channelId = optionalChannelId.get();
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

    private MessageCreateData buildAnnouncementMessage(Emoji emoji, Guild guild, Member member) {
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(resourceBundle.getString("listener.emote.announce.message"));
        if (member != null) {
            messageBuilder.addContent(" ");
            messageBuilder.addContent(resourceBundle.getString("listener.emote.announce.message.member"));
            messageBuilder.addContent(member.getEffectiveName());
        }
        messageBuilder.addContent("\n");
        messageBuilder.addContent(emoji.getFormatted());
        return messageBuilder.build();
    }
}

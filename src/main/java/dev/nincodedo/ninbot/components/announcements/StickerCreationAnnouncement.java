package dev.nincodedo.ninbot.components.announcements;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Slf4j
@Component
public class StickerCreationAnnouncement extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public StickerCreationAnnouncement(StatManager statManager, ConfigService configService,
            ComponentService componentService) {
        super(statManager);
        this.configService = configService;
        this.componentService = componentService;
        componentName = "sticker-added-announcement";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onGuildStickerAdded(@NotNull GuildStickerAddedEvent event) {
        if (componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        var optionalChannelId = configService.getSingleValueByName(event.getGuild()
                .getId(), ConfigConstants.EMOTE_ADDED_ANNOUNCEMENT_CHANNEL_ID);
        optionalChannelId.ifPresent(channelId -> {
            var channel = (GuildMessageChannel) event.getGuild().getGuildChannelById(channelId);
            if (channel == null) {
                return;
            }
            var sticker = event.getSticker();
            var member = getMemberFromAudit(event.getJDA().getSelfUser(), event.getGuild(), channel);
            channel.sendMessage(buildAnnouncementMessage(sticker, event.getGuild(), member))
                    .queue(message -> countOneStat(componentName, event.getGuild().getId()));
        });
    }

    private Message buildAnnouncementMessage(GuildSticker sticker, Guild guild, Member member) {
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append(resourceBundle.getString("listener.sticker.announce.message"));
        if (member != null) {
            messageBuilder.append(" ")
                    .append(resourceBundle.getString("listener.sticker.announce.message.member"))
                    .append(member);
        }
        messageBuilder.setStickers(sticker);
        return messageBuilder.build();
    }

    private Member getMemberFromAudit(User user, Guild guild, GuildMessageChannel channel) {
        var member = guild.getMember(user);
        if (member != null && member.getPermissions(channel).contains(Permission.VIEW_AUDIT_LOGS)) {
            var recentAuditUser = guild.retrieveAuditLogs().complete().get(0).getUser();
            if (recentAuditUser == null) {
                return null;
            }
            return guild.getMember(recentAuditUser);
        } else {
            return null;
        }
    }
}

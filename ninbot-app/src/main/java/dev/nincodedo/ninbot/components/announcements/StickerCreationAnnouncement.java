package dev.nincodedo.ninbot.components.announcements;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.ComponentType;
import dev.nincodedo.nincord.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class StickerCreationAnnouncement extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public StickerCreationAnnouncement(StatManager statManager,
            @Qualifier("statCounterThreadPool") ExecutorService executorService, ConfigService configService,
            ComponentService componentService) {
        super(statManager, executorService);
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
            channel.sendMessage(buildAnnouncementMessage(event.getGuild(), member))
                    .setStickers(sticker)
                    .queue(message -> countOneStat(componentName, event.getGuild().getId()));
        });
    }

    private MessageCreateData buildAnnouncementMessage(Guild guild, Member member) {
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(resourceBundle.getString("listener.sticker.announce.message"));
        if (member != null) {
            messageBuilder.addContent(" ")
                    .addContent(resourceBundle.getString("listener.sticker.announce.message.member"))
                    .addContent(member.getEffectiveName());
        }
        messageBuilder.addContent("!");
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

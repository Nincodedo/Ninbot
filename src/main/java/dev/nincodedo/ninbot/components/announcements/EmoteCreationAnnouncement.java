package dev.nincodedo.ninbot.components.announcements;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class EmoteCreationAnnouncement extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public EmoteCreationAnnouncement(StatManager statManager, ConfigService configService,
            ComponentService componentService) {
        super(statManager);
        this.componentService = componentService;
        this.configService = configService;
        componentName = "emote-added-announcement";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onEmoteAdded(EmoteAddedEvent event) {
        if (componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        var optionalChannelId = configService.getSingleValueByName(event.getGuild()
                .getId(), ConfigConstants.EMOTE_ADDED_ANNOUNCEMENT_CHANNEL_ID);
        if (optionalChannelId.isPresent()) {
            var emoteAddedChannelId = optionalChannelId.get();
            var channel = event.getJDA().getTextChannelById(emoteAddedChannelId);
            if (channel != null) {
                var emote = event.getEmote();
                countOneStat(componentName, event.getGuild().getId());
                Member member = null;
                if (event.getGuild()
                        .getMember(event.getJDA().getSelfUser())
                        .getPermissions(channel)
                        .contains(Permission.VIEW_AUDIT_LOGS)) {
                    member = event.getGuild()
                            .getMember(event.getGuild().retrieveAuditLogs().complete().get(0).getUser());
                }
                channel.sendMessage(buildAnnouncementMessage(emote, event.getGuild(), member))
                        .queue(message -> message.addReaction(emote).queue());
            }
        }
    }

    @NotNull
    private Message buildAnnouncementMessage(Emote emote, Guild guild, Member member) {
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(guild);
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append(resourceBundle.getString("listener.emote.announce.message"));
        if (member != null) {
            messageBuilder.append(" ");
            messageBuilder.append(resourceBundle.getString("listener.emote.announce.message.member"));
            messageBuilder.append(member);
        }
        messageBuilder.append("\n");
        messageBuilder.append(emote);
        return messageBuilder.build();
    }
}

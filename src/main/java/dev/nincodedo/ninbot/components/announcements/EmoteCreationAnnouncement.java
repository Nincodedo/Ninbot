package dev.nincodedo.ninbot.components.announcements;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.val;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class EmoteCreationAnnouncement extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private LocaleService localeService;
    private String componentName;

    public EmoteCreationAnnouncement(StatManager statManager, ConfigService configService, LocaleService localeService,
            ComponentService componentService) {
        super(statManager);
        this.componentService = componentService;
        this.configService = configService;
        this.localeService = localeService;
        componentName = "emote-added-announcement";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onEmoteAdded(EmoteAddedEvent event) {
        if (componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        val optionalChannelId = configService.getSingleValueByName(event.getGuild()
                .getId(), ConfigConstants.EMOTE_ADDED_ANNOUNCEMENT_CHANNEL_ID);
        if (optionalChannelId.isPresent()) {
            val emoteAddedChannelId = optionalChannelId.get();
            val channel = event.getJDA().getTextChannelById(emoteAddedChannelId);
            if (channel != null) {
                val emote = event.getEmote();
                countOneStat(componentName, event.getGuild().getId());
                channel.sendMessage(buildAnnouncementMessage(emote, event.getGuild())).queue();
            }
        }
    }

    @NotNull
    private Message buildAnnouncementMessage(Emote emote, Guild guild) {
        ResourceBundle resourceBundle = localeService.getResourceBundleOrDefault(guild);
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append(resourceBundle.getString("listener.emote.announce.message"));
        messageBuilder.append("\n");
        messageBuilder.append(emote);
        return messageBuilder.build();
    }
}

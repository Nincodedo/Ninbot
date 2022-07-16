package dev.nincodedo.ninbot.components.pathogen;

import dev.nincodedo.ninbot.common.BaseListenerAdapter;
import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PathogenSpreadListener extends BaseListenerAdapter {

    private PathogenManager pathogenManager;
    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public PathogenSpreadListener(PathogenManager pathogenManager, ComponentService componentService,
            ConfigService configService, ServerLoggerFactory serverLoggerFactory) {
        super(serverLoggerFactory);
        this.pathogenManager = pathogenManager;
        this.componentService = componentService;
        this.configService = configService;
        this.componentName = "pathogen";
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || componentService.isDisabled(componentName, event.getGuild().getId())
                || configService.isConfigEnabled(ConfigConstants.PATHOGEN_DENY_LIST_CHANNEL, event.getGuild()
                .getId(), event.getChannel().getId())
                || !pathogenManager.isSpreadableEvent(event)) {
            return;
        }
        var guildId = event.getGuild().getId();
        int messageSearchLimit = configService.getGlobalConfigByName(ConfigConstants.PATHOGEN_MESSAGE_SEARCH_LIMIT,
                guildId).map(config -> Integer.parseInt(config.getValue())).orElse(3);
        int messageAffectChance = configService.getGlobalConfigByName(ConfigConstants.PATHOGEN_MESSAGE_AFFECT_CHANCE
                , guildId).map(config -> Integer.parseInt(config.getValue())).orElse(40);

        event.getChannel().getHistoryAround(event.getMessage(), messageSearchLimit).queue(messageHistory -> {
            Map<User, Message> surroundingUsers = new HashMap<>();
            messageHistory.getRetrievedHistory().forEach(message -> surroundingUsers.put(message.getAuthor(), message));
            pathogenManager.spread(event.getGuild(), event.getAuthor(), surroundingUsers, messageAffectChance);
        });
    }
}

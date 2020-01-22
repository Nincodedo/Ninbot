package com.nincraft.ninbot.components.fun.pathogen;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PathogenSpreadListener extends ListenerAdapter {

    private PathogenManager pathogenManager;
    private ComponentService componentService;
    private ConfigService configService;
    private String componentName;

    public PathogenSpreadListener(PathogenManager pathogenManager, ComponentService componentService,
            ConfigService configService) {
        this.pathogenManager = pathogenManager;
        this.componentService = componentService;
        this.configService = configService;
        this.componentName = "pathogen";
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || componentService.isDisabled(componentName, event.getGuild().getId())
                || !pathogenManager.isSpreadableEvent(event)) {
            return;
        }
        val serverId = event.getGuild().getId();
        int messageSearchLimit = configService.getGlobalConfigByName(ConfigConstants.PATHOGEN_MESSAGE_SEARCH_LIMIT,
                serverId).map(config -> Integer.parseInt(config.getValue())).orElse(3);
        int messageAffectChance = configService.getGlobalConfigByName(ConfigConstants.PATHOGEN_MESSAGE_AFFECT_CHANCE
                , serverId).map(config -> Integer.parseInt(config.getValue())).orElse(40);

        event.getChannel().getHistoryAround(event.getMessage(), messageSearchLimit).queue(messageHistory -> {
            Map<User, Message> surroundingUsers = new HashMap<>();
            messageHistory.getRetrievedHistory().forEach(message -> surroundingUsers.put(message.getAuthor(), message));
            pathogenManager.spread(event.getGuild(), event.getAuthor(), surroundingUsers, messageAffectChance);
        });
    }
}

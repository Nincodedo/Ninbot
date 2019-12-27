package com.nincraft.ninbot.components.fun.pathogen;

import com.nincraft.ninbot.components.config.component.ComponentService;
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
    private String componentName;

    public PathogenSpreadListener(PathogenManager pathogenManager, ComponentService componentService) {
        this.pathogenManager = pathogenManager;
        this.componentService = componentService;
        this.componentName = "pathogen";
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || componentService.isDisabled(componentName, event.getGuild().getId())
                || !pathogenManager.isSpreadableEvent(event)) {
            return;
        }
        event.getChannel().getHistoryAround(event.getMessage(), 5).queue(messageHistory -> {
            Map<User, Message> surroundingUsers = new HashMap<>();
            messageHistory.getRetrievedHistory().forEach(message -> surroundingUsers.put(message.getAuthor(), message));
            pathogenManager.spread(event.getGuild(), surroundingUsers);
        });
    }
}

package dev.nincodedo.ninbot.components.reaction;

import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class ReactionListener extends ListenerAdapter {

    private List<ReactionResponse> reactionResponseList;
    private ComponentService componentService;
    private String componentName;

    @Autowired
    public ReactionListener(List<ReactionResponse> reactionResponseList, ComponentService componentService) {
        this.reactionResponseList = reactionResponseList;
        this.componentService = componentService;
        this.componentName = "reaction-listener";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && !event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId())) {
            respond(event);
        }
    }

    private void respond(MessageReceivedEvent event) {
        reactionResponseList.stream()
                .filter(reactionResponse -> reactionResponse.canRespond(event))
                .findFirst()
                .ifPresent(reactionResponse -> reactionResponse.react(event.getMessage(), event.getChannel()));
    }
}

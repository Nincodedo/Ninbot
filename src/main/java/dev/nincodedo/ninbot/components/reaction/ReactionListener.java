package dev.nincodedo.ninbot.components.reaction;

import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactionListener extends StatAwareListenerAdapter {

    private List<ReactionResponse> reactionResponseList;
    private ComponentService componentService;
    private StatManager statManager;
    private String componentName;

    @Autowired
    public ReactionListener(List<ReactionResponse> reactionResponseList, ComponentService componentService,
            StatManager statManager) {
        super(statManager);
        this.reactionResponseList = reactionResponseList;
        this.componentService = componentService;
        this.statManager = statManager;
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
                .ifPresent(reactionResponse -> {
                    reactionResponse.react(event.getMessage(), event.getChannel());
                    statManager.addOneCount(reactionResponse.getResponse()
                            .toLowerCase(), StatCategory.REACTION, event.getGuild().getId());
                    countOneStat(componentName, event.getGuild().getId());
                });
    }
}

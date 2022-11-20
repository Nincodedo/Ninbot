package dev.nincodedo.ninbot.components.reaction;

import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.ComponentType;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.stats.StatCategory;
import dev.nincodedo.nincord.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
class ReactionListener extends StatAwareListenerAdapter {

    private List<ReactionResponse> reactionResponseList;
    private ComponentService componentService;
    private StatManager statManager;
    private String componentName;

    @Autowired
    public ReactionListener(List<ReactionResponse> reactionResponseList, ComponentService componentService,
            StatManager statManager, @Qualifier("statCounterThreadPool") ExecutorService executorService) {
        super(statManager, executorService);
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
            try {
                respond(event);
            } catch (Exception e) {
                log.error("Reaction listener error in server {} in channel {}",
                        FormatLogObject.guildInfo(event.getGuild()), FormatLogObject.channelInfo(event.getChannel()),
                        e);
            }
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

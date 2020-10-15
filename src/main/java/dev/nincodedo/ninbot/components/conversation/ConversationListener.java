package dev.nincodedo.ninbot.components.conversation;

import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.sapconversational.SAPConversationalAIAPI;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Log4j2
@Component
public class ConversationListener extends StatAwareListenerAdapter {

    private ConfigService configService;
    private ComponentService componentService;
    private SAPConversationalAIAPI sapConversationalAIAPI;
    private String componentName;

    public ConversationListener(ConfigService configService, ComponentService componentService,
            SAPConversationalAIAPI sapConversationalAIAPI) {
        this.configService = configService;
        this.componentService = componentService;
        this.sapConversationalAIAPI = sapConversationalAIAPI;
        this.componentName = "conversation-listener";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if ((event.isFromGuild() && componentService.isDisabled(componentName, event.getGuild().getId()))
                || !event.isFromGuild()) {
            return;
        }
        val channelId = event.getChannel().getId();
        val message = event.getMessage().getContentStripped();
        if (!event.getAuthor().isBot() && isConversationEnabledChannel(event.getGuild().getId(), channelId)
                && isNormalConversation(message)) {
            val botConversation = sapConversationalAIAPI.startBotConversation(channelId);
            botConversation.addParticipants(event.getAuthor().getName());
            try {
                botConversation.getResponse(message).get().ifPresent(response ->
                        event.getChannel()
                                .sendMessage(response)
                                .queue(message1 -> countOneStat(componentName, event.getGuild().getId()))
                );
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to get conversation response", e);
            }
        }
    }

    private boolean isNormalConversation(String message) {
        return !message.toLowerCase().startsWith("@ninbot") && message.toLowerCase().contains("ninbot");
    }

    private boolean isConversationEnabledChannel(String serverId, String channelId) {
        val channelList = configService.getValuesByName(serverId, ConfigConstants.CONVERSATION_CHANNELS);
        return channelList.contains(channelId);
    }
}

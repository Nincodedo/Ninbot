package dev.nincodedo.ninbot.components.conversation;

import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import dev.nincodedo.sapconversational.SAPConversationalAIAPI;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class ConversationListener extends StatAwareListenerAdapter {

    private static final org.apache.logging.log4j.Logger log =
            org.apache.logging.log4j.LogManager.getLogger(ConversationListener.class);
    private ConfigService configService;
    private ComponentService componentService;
    private SAPConversationalAIAPI sapConversationalAIAPI;
    private String componentName;

    public ConversationListener(ConfigService configService, ComponentService componentService,
            SAPConversationalAIAPI sapConversationalAIAPI, StatManager statManager) {
        super(statManager);
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
        String channelId = event.getChannel().getId();
        String message = event.getMessage().getContentStripped();
        if (!event.getAuthor().isBot() && isConversationEnabledChannel(event.getGuild().getId(), channelId)
                && isNormalConversation(message)) {
            final dev.nincodedo.sapconversational.conversation.BotConversation botConversation =
                    sapConversationalAIAPI.startBotConversation(channelId);
            botConversation.addParticipants(event.getAuthor().getName());
            try {
                botConversation.getResponse(message)
                        .get()
                        .ifPresent(response -> event.getChannel()
                                .sendMessage(response)
                                .queue(message1 -> countOneStat(componentName, event.getGuild().getId())));
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to get conversation response", e);
            }
        }
    }

    private boolean isNormalConversation(String message) {
        return !message.toLowerCase().startsWith("@ninbot") && message.toLowerCase().contains("ninbot");
    }

    private boolean isConversationEnabledChannel(String serverId, String channelId) {
        final java.util.List<java.lang.String> channelList = configService.getValuesByName(serverId, ConfigConstants.CONVERSATION_CHANNELS);
        return channelList.contains(channelId);
    }
}

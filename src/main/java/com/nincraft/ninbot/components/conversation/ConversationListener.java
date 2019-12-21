package com.nincraft.ninbot.components.conversation;

import com.nincodedo.sapconversational.SAPConversationalAIAPI;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import com.nincraft.ninbot.components.config.component.ComponentType;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ConversationListener extends ListenerAdapter {

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
            botConversation.getResponse(message).ifPresent(response ->
                    event.getChannel().sendMessage(response).queue()
            );
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

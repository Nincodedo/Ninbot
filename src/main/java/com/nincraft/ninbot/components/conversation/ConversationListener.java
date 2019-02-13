package com.nincraft.ninbot.components.conversation;

import com.nincodedo.recast.RecastAPI;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ConversationListener extends ListenerAdapter {

    private ConfigService configService;
    private RecastAPI recastAPI;

    public ConversationListener(ConfigService configService, RecastAPI recastAPI) {
        this.configService = configService;
        this.recastAPI = recastAPI;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        val channelId = event.getChannel().getId();
        val message = event.getMessage().getContentStripped();
        if (!event.getAuthor().isBot() && isConversationEnabledChannel(event.getGuild().getId(), channelId)
                && isNormalConversation(message)) {
            val botConversation = recastAPI.startBotConversation(channelId);
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

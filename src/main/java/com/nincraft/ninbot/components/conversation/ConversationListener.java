package com.nincraft.ninbot.components.conversation;

import com.nincodedo.recast.RecastAPI;
import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ConversationListener extends ListenerAdapter {

    private ConfigService configService;
    private RecastAPI recastAPI;
    private MessageUtils messageUtils;

    public ConversationListener(ConfigService configService, RecastAPI recastAPI, MessageUtils messageUtils) {
        this.configService = configService;
        this.recastAPI = recastAPI;
        this.messageUtils = messageUtils;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        val channelId = event.getChannel().getId();
        val message = event.getMessage().getContentStripped();
        if (!event.getAuthor().isBot() && isNormalConversation(message)
                && isConversationEnabledChannel(event.getGuild().getId(), channelId)) {
            val botConversation = recastAPI.startBotConversation(channelId);
            botConversation.addParticipants(event.getAuthor().getName());
            botConversation.getResponse(message).ifPresent(response ->
                    messageUtils.sendMessage(event.getChannel(), response)
            );
        }
    }

    private boolean isNormalConversation(String message) {
        return !message.toLowerCase().startsWith("@ninbot");
    }

    private boolean isConversationEnabledChannel(String serverId, String channelId) {
        val channelList = configService.getValuesByName(serverId, ConfigConstants.CONVERSATION_CHANNELS);
        return channelList.contains(channelId);
    }
}

package com.nincraft.ninbot.components.channel;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TopicChangeListener extends ListenerAdapter {

    private ConfigService configService;

    public TopicChangeListener(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {
        val channelIds = configService.getValuesByName(event.getGuild().getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);
        val eventChannel = event.getChannel();
        if (StringUtils.isNotBlank(event.getNewTopic()) && channelIds.contains(eventChannel.getId())) {
            String message;
            if (event.getGuild().getMember(event.getJDA().getSelfUser()).getPermissions(eventChannel).contains(Permission.VIEW_AUDIT_LOGS)) {
                val auditLogs = event.getGuild().getAuditLogs().complete();
                message = String.format("%s updated topic to %s",
                        event.getGuild().getMember(auditLogs.get(0).getUser()).getEffectiveName(),
                        auditLogs.get(0).getChangeByKey("topic").getNewValue());
            } else {
                message = String.format("Topic updated to %s", event.getNewTopic());
            }
            eventChannel.sendMessage(message).queue();
        }
    }
}

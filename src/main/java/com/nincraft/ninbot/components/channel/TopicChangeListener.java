package com.nincraft.ninbot.components.channel;

import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class TopicChangeListener extends ListenerAdapter {

    private ConfigService configService;
    private LocaleService localeService;

    public TopicChangeListener(ConfigService configService, LocaleService localeService) {
        this.configService = configService;
        this.localeService = localeService;
    }

    @Override
    public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {
        val channelIds = configService.getValuesByName(event.getGuild().getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);
        val eventChannel = event.getChannel();
        if (StringUtils.isNotBlank(event.getNewTopic()) && channelIds.contains(eventChannel.getId())) {
            String message;
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", localeService.getLocale(event.getGuild()
                    .getId()));
            if (event.getGuild()
                    .getMember(event.getJDA().getSelfUser())
                    .getPermissions(eventChannel)
                    .contains(Permission.VIEW_AUDIT_LOGS)) {
                val auditLogs = event.getGuild().retrieveAuditLogs().complete();
                message = String.format(resourceBundle.getString("listener.topic.updated.withpermission"),
                        event.getGuild().getMember(auditLogs.get(0).getUser()).getEffectiveName(),
                        auditLogs.get(0).getChangeByKey("topic").getNewValue());
            } else {
                message = String.format(resourceBundle.getString("listener.topic.update.nopermission"),
                        event.getNewTopic());
            }
            eventChannel.sendMessage(message).queue();
        }
    }
}

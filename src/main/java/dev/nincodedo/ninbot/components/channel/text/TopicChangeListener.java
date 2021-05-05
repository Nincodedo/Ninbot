package dev.nincodedo.ninbot.components.channel.text;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateTopicEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class TopicChangeListener extends StatAwareListenerAdapter {

    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;

    public TopicChangeListener(ConfigService configService,
            ComponentService componentService, StatManager statManager) {
        super(statManager);
        this.configService = configService;
        this.componentService = componentService;
        this.componentName = "topic-change";
    }

    @Override
    public void onTextChannelUpdateTopic(TextChannelUpdateTopicEvent event) {
        if (componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        val channelIds = configService.getValuesByName(event.getGuild().getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);
        val eventChannel = event.getChannel();
        if (StringUtils.isNotBlank(event.getNewTopic()) && (channelIds.contains(eventChannel.getId())
                || channelIds.contains("*"))) {
            String message;
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", LocaleService.getLocale(event.getGuild()));
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
            countOneStat(componentName, event.getGuild().getId());
            eventChannel.sendMessage(message).queue();
        }
    }
}

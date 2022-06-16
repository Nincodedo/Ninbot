package dev.nincodedo.ninbot.components.channel.text;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.logging.ServerLogger;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class TopicChangeListener extends StatAwareListenerAdapter {

    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;

    public TopicChangeListener(ConfigService configService,
            ComponentService componentService, StatManager statManager, ServerLogger serverLogger) {
        super(serverLogger, statManager);
        this.configService = configService;
        this.componentService = componentService;
        this.componentName = "topic-change";
    }

    @Override
    public void onChannelUpdateTopic(ChannelUpdateTopicEvent event) {
        if (!event.getChannelType().isGuild()) {
            return;
        }
        var channel = (GuildMessageChannel) event.getChannel();
        onGuildMessageChannelUpdateTopic(channel, channel.getGuild(), event.getNewValue());
    }

    private void onGuildMessageChannelUpdateTopic(GuildMessageChannel channel, Guild guild, String newValue) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        var channelIds = configService.getValuesByName(guild.getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);
        if (StringUtils.isNotBlank(newValue) && (channelIds.contains(channel.getId())
                || channelIds.contains("*"))) {
            String message;
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", LocaleService.getLocale(guild));
            if (guild
                    .getMember(guild.getJDA().getSelfUser())
                    .getPermissions(channel)
                    .contains(Permission.VIEW_AUDIT_LOGS)) {
                var auditLogs = guild.retrieveAuditLogs().complete();
                message = String.format(resourceBundle.getString("listener.topic.updated.withpermission"),
                        guild.getMember(auditLogs.get(0).getUser()).getEffectiveName(),
                        auditLogs.get(0).getChangeByKey("topic").getNewValue());
            } else {
                message = String.format(resourceBundle.getString("listener.topic.update.nopermission"),
                        newValue);
            }
            countOneStat(componentName, guild.getId());
            channel.sendMessage(message).queue();
        }
    }
}

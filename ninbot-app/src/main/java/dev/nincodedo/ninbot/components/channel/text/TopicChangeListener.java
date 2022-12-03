package dev.nincodedo.ninbot.components.channel.text;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.stats.StatManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

@Component
public class TopicChangeListener extends StatAwareListenerAdapter {

    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;

    public TopicChangeListener(ConfigService configService,
            @Qualifier("statCounterThreadPool") ExecutorService executorService,
            ComponentService componentService, StatManager statManager) {
        super(statManager, executorService);
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

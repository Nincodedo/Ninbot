package dev.nincodedo.ninbot.components.fun.dad;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.common.message.MessageUtils;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

@Component
public class Dadbot extends StatAwareListenerAdapter {

    private Random random;
    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);

    @Autowired
    public Dadbot(ConfigService configService, ComponentService componentService, StatManager statManager) {
        super(statManager);
        random = new Random();
        this.configService = configService;
        componentName = "dad";
        this.componentService = componentService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && !event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId())) {
            resourceBundle = LocaleService.getResourceBundleOrDefault(event.getGuild());
            parseMessage(event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        var strippedMessage = event.getMessage().getContentStripped();
        var first = strippedMessage.split("\\s+")[0];
        if (!(first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imcontraction"))
                || first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imnocontraction")))
                || (!event.getChannelType().isGuild())) {
            return;
        }
        if (StringUtils.isNotBlank(strippedMessage) && strippedMessage.split("\\s+").length >= 1 && checkChance()) {
            hiImDad(event.getMessage(), event);
        }
    }

    private boolean channelIsOnDenyList(String serverId, String channelId) {
        var channelConfigList = configService.getConfigByName(serverId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL);
        return channelConfigList.stream().anyMatch(config -> config.getValue().equals(channelId));
    }

    private void hiImDad(Message message, MessageReceivedEvent event) {
        if (channelIsOnDenyList(event.getGuild().getId(), event.getChannel().getId())) {
            return;
        }
        String strippedMessage = message.getContentStripped();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(resourceBundle.getString("listener.dad.hi")).append(" ");
        stringBuilder.append(MessageUtils.addSpoilerText(strippedMessage.substring(strippedMessage.indexOf(' '))
                .trim(), message.getContentRaw()));
        stringBuilder.append(resourceBundle.getString("listener.dad.imdad"));
        event.getChannel()
                .sendMessage(stringBuilder)
                .queue(message1 -> countOneStat(componentName, event.getGuild().getId()));
    }

    private boolean checkChance() {
        return random.nextInt(100) < 10;
    }
}

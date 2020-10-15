package dev.nincodedo.ninbot.components.fun.dad;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import lombok.val;
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
    private LocaleService localeService;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);

    @Autowired
    public Dadbot(ConfigService configService, ComponentService componentService, LocaleService localeService) {
        random = new Random();
        this.configService = configService;
        componentName = "dad";
        this.componentService = componentService;
        this.localeService = localeService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && !event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId())) {
            resourceBundle = localeService.getResourceBundleOrDefault(event.getGuild());
            parseMessage(event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val first = message.split("\\s+")[0];
        if (!(first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imcontraction"))
                || first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imnocontraction")))
                || (!event.getChannelType().isGuild())) {
            return;
        }
        if (StringUtils.isNotBlank(message) && message.split("\\s+").length >= 1 && checkChance()) {
            hiImDad(message, event);
        }
    }

    private boolean channelIsOnDenyList(String serverId, String channelId) {
        val channelConfigList = configService.getConfigByName(serverId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL);
        return channelConfigList.stream().anyMatch(config -> config.getValue().equals(channelId));
    }

    private void hiImDad(String message, MessageReceivedEvent event) {
        if (channelIsOnDenyList(event.getGuild().getId(), event.getChannel().getId())) {
            return;
        }
        String stringBuilder = resourceBundle.getString("listener.dad.hi") +
                message.substring(message.indexOf(' ')) +
                resourceBundle.getString("listener.dad.imdad");
        event.getChannel()
                .sendMessage(stringBuilder)
                .queue(message1 -> countOneStat(componentName, event.getGuild().getId()));
    }

    private boolean checkChance() {
        return random.nextInt(100) < 10;
    }
}

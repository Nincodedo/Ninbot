package dev.nincodedo.ninbot.components.dad;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageReceivedEventMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageUtils;
import dev.nincodedo.ninbot.common.message.impersonation.Impersonation;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Component
public class Dadbot extends StatAwareListenerAdapter {

    private static final int DISCORD_NICKNAME_LENGTH_LIMIT = 32;
    private Random random;
    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);
    private Impersonation dadbotImpersonation;

    @Autowired
    public Dadbot(ConfigService configService, ComponentService componentService, StatManager statManager) {
        super(statManager);
        random = new SecureRandom();
        this.configService = configService;
        componentName = "dad";
        this.componentService = componentService;
        this.dadbotImpersonation = Impersonation.of("Dadbot", "https://i.imgur.com/zfKodNp.png");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && !event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId())) {
            resourceBundle = LocaleService.getResourceBundleOrDefault(event.getGuild());
            parseMessage(event).executeActions();
        }
    }

    private MessageExecutor<MessageReceivedEventMessageExecutor> parseMessage(MessageReceivedEvent event) {
        var messageExecutor = new MessageReceivedEventMessageExecutor(event);
        var strippedMessage = event.getMessage().getContentStripped();
        var first = strippedMessage.split("\\s+")[0];
        if (!(first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imcontraction"))
                || first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imnocontraction")))
                || !event.getChannelType().isGuild()) {
            return messageExecutor;
        }
        if (StringUtils.isNotBlank(strippedMessage) && strippedMessage.split("\\s+").length >= 1 && checkChance()) {
            hiImDad(event.getMessage(), event, messageExecutor);
        }
        return messageExecutor;
    }

    private boolean channelIsOnDenyList(String guildId, String channelId) {
        var channelConfigList = configService.getConfigByName(guildId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL);
        return channelConfigList.stream().anyMatch(config -> config.getValue().equals(channelId));
    }

    private void hiImDad(Message message, MessageReceivedEvent event,
            MessageReceivedEventMessageExecutor messageExecutor) {
        if (channelIsOnDenyList(event.getGuild().getId(), event.getChannel().getId())) {
            return;
        }
        messageExecutor.impersonate(dadbotImpersonation);
        String strippedMessage = message.getContentStripped();

        String dadName = MessageUtils.addSpoilerText(strippedMessage.substring(strippedMessage.indexOf(' '))
                .trim(), message.getContentRaw());
        String dadResponse = resourceBundle.getString("listener.dad.hi") + " "
                + dadName
                + resourceBundle.getString("listener.dad.imdad");
        messageExecutor.addMessageResponse(dadResponse);
        dadJoke(dadName, message.getMember());
        countOneStat(componentName, event.getGuild().getId());
    }

    private void dadJoke(String dadName, Member member) {
        var self = member.getGuild().getSelfMember();
        if (dadName.length() > DISCORD_NICKNAME_LENGTH_LIMIT || !self.hasPermission(Permission.NICKNAME_MANAGE)
                || !self.canInteract(member)) {
            return;
        }
        var oldName = member.getNickname();
        member.modifyNickname(StringUtils.capitalize(dadName))
                .reason("Dad joke")
                .queue(success -> member.modifyNickname(oldName)
                        .reason("Dad joke done")
                        .queueAfter(2, TimeUnit.MINUTES));
    }

    private boolean checkChance() {
        return random.nextInt(100) < 10;
    }
}

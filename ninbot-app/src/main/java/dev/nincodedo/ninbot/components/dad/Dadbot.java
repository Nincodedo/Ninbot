package dev.nincodedo.ninbot.components.dad;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.Config;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.ComponentType;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.MessageReceivedEventMessageExecutor;
import dev.nincodedo.nincord.message.impersonation.Impersonation;
import dev.nincodedo.nincord.stats.StatManager;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class Dadbot extends StatAwareListenerAdapter {

    private static final int DISCORD_NICKNAME_LENGTH_LIMIT = 32;
    private Random random;
    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;
    private DadbotMessageParser dadbotMessageParser;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);
    private Impersonation dadbotImpersonation;
    @Getter
    private HashMap<String, List<String>> originalNicknames;

    public Dadbot(StatManager statManager, @Qualifier("statCounterThreadPool") ExecutorService executorService,
            ConfigService configService, ComponentService componentService) {
        super(statManager, executorService);
        random = new SecureRandom();
        this.configService = configService;
        componentName = "dad";
        this.componentService = componentService;
        this.componentService.registerComponent(componentName, ComponentType.ACTION);
        this.dadbotMessageParser = new DadbotMessageParser();
        this.dadbotImpersonation = Impersonation.of("Dadbot", "https://i.imgur.com/zfKodNp.png");
        this.originalNicknames = new HashMap<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && !event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId(), event.getAuthor().getId())) {
            resourceBundle = LocaleService.getResourceBundleOrDefault(event.getGuild());
            parseMessage(event).executeActions();
        }
    }

    private MessageExecutor parseMessage(MessageReceivedEvent event) {
        var messageExecutor = new MessageReceivedEventMessageExecutor(event);
        var strippedMessage = event.getMessage().getContentStripped();
        var rawMessage = event.getMessage().getContentRaw();
        var optionalDadJoke = dadbotMessageParser.dadReply(strippedMessage, rawMessage);
        if (optionalDadJoke.isEmpty() || !event.getChannelType().isGuild()) {
            return messageExecutor;
        }
        if (checkChance(event.getGuild().getId())) {
            hiImDad(event, messageExecutor, optionalDadJoke.get());
        }
        return messageExecutor;
    }

    private boolean channelIsOnDenyList(String guildId, String channelId) {
        var channelConfigList = configService.getConfigByName(guildId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL);
        return channelConfigList.stream().anyMatch(config -> channelId.equals(config.getValue()));
    }

    private void hiImDad(MessageReceivedEvent event,
            MessageReceivedEventMessageExecutor messageExecutor, String dadName) {
        if (channelIsOnDenyList(event.getGuild().getId(), event.getChannel().getId())) {
            return;
        }
        messageExecutor.impersonate(dadbotImpersonation);

        String dadResponse = String.format(resourceBundle.getString("listener.dad.joke"), dadName);
        messageExecutor.addMessageResponse(dadResponse);
        dadJoke(dadName, event.getMember());
        countOneStat(componentName, event.getGuild().getId());
    }

    protected void dadJoke(String dadName, Member member) {
        if (dadName.length() > DISCORD_NICKNAME_LENGTH_LIMIT || member == null) {
            return;
        }
        var self = member.getGuild().getSelfMember();
        if (!self.hasPermission(Permission.NICKNAME_MANAGE)
                || !self.canInteract(member)) {
            return;
        }
        var memberLocation = String.format("%s-%s", member.getGuild().getId(), member.getId());
        originalNicknames.computeIfAbsent(memberLocation, _ -> new ArrayList<>());
        originalNicknames.get(memberLocation).add(member.getNickname());
        member.modifyNickname(WordUtils.capitalizeFully(dadName))
                .reason("Dad joke")
                .queue(restoreNickname(member, memberLocation));
    }

    private @NotNull Consumer<Void> restoreNickname(Member member, String memberLocation) {
        return _ -> {
            var originalNicknameList = originalNicknames.get(memberLocation);
            var originalNickname = originalNicknameList.removeLast();
            if (originalNicknameList.isEmpty()) {
                originalNicknames.remove(memberLocation);
            }
            member.modifyNickname(originalNickname)
                    .reason("Dad joke done")
                    .queueAfter(2, TimeUnit.MINUTES);
        };
    }

    private boolean checkChance(String guildId) {
        int chance = configService.getGlobalConfigByName(ConfigConstants.DADBOT_REPLY_CHANCE, guildId)
                .stream()
                .map(Config::getValue)
                .filter(Objects::nonNull)
                .map(Integer::parseInt)
                .findFirst()
                .orElse(10);
        return random.nextInt(100) < chance;
    }
}

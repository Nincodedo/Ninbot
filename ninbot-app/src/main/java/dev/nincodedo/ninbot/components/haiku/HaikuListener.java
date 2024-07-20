package dev.nincodedo.ninbot.components.haiku;

import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.ComponentType;
import dev.nincodedo.nincord.message.MessageUtils;
import dev.nincodedo.nincord.stats.StatManager;
import io.micrometer.core.instrument.Metrics;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ExecutorService;

@Component
public class HaikuListener extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private HaikuMessageParser haikuMessageParser;
    private Random random;
    private String componentName;

    public HaikuListener(StatManager statManager, @Qualifier("statCounterThreadPool") ExecutorService executorService
            , ComponentService componentService, ConfigService configService, HaikuMessageParser haikuMessageParser) {
        super(statManager, executorService);
        this.componentService = componentService;
        this.configService = configService;
        this.haikuMessageParser = haikuMessageParser;
        this.random = new SecureRandom();
        this.componentName = "haiku";
        componentService.registerComponent(componentName, ComponentType.ACTION);
    }

    @WithSpan
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()
                || componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        var message = event.getMessage().getContentStripped();
        var guildId = event.getGuild().getId();
        Metrics.counter("bot.listener.haiku.checked").increment();
        haikuMessageParser.isHaikuable(message).ifPresent(haikuLines -> {
            if (!checkChance(guildId)) {
                return;
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.appendDescription(MessageUtils.addSpoilerText(
                    "_" + haikuLines + "_", event.getMessage().getContentRaw()));
            embedBuilder.setFooter("A haiku inspired by " + event.getMember().getEffectiveName());
            event.getChannel()
                    .sendMessage(new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build())
                    .queue(message1 -> countOneStat(componentName, event.getGuild().getId()));
            Metrics.counter("bot.listener.haiku.success").increment();
        });
    }

    boolean checkChance(String guildId) {
        var optionalConfig = configService.getGlobalConfigByName(ConfigConstants.HAIKU_CHANCE, guildId);
        int chance = 10;
        if (optionalConfig.isPresent()) {
            chance = Integer.parseInt(optionalConfig.get().getValue());
        }
        return random.nextInt(100) < chance;
    }
}

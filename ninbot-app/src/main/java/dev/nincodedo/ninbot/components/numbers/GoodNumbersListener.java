package dev.nincodedo.ninbot.components.numbers;

import dev.nincodedo.nincord.LocaleService;
import dev.nincodedo.nincord.StatAwareListenerAdapter;
import dev.nincodedo.nincord.config.db.Config;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.config.db.component.ComponentService;
import dev.nincodedo.nincord.config.db.component.ComponentType;
import dev.nincodedo.nincord.logging.FormatLogObject;
import dev.nincodedo.nincord.stats.StatManager;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class GoodNumbersListener extends StatAwareListenerAdapter {

    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;

    protected GoodNumbersListener(ConfigService configService, StatManager statManager, @Qualifier(
            "statCounterThreadPool") ExecutorService executorService, ComponentService componentService) {
        super(statManager, executorService);
        this.configService = configService;
        this.componentService = componentService;
        this.componentName = "good-numbers";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Timed("listener.goodnumbers")
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild()
                || componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        var message = event.getMessage().getContentStripped();
        Pattern pattern = Pattern.compile("(-?\\d+)");
        Matcher matcher = pattern.matcher(message);
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            try {
                numbers.add(Integer.parseInt(matcher.group(1)));
            } catch (NumberFormatException e) {
                //cool
            }
        }
        if (numbers.size() <= 1) {
            return;
        }
        var total = numbers.stream().mapToInt(Integer::intValue).sum();
        var goodNumbers = configService.getGlobalConfigsByName(ConfigConstants.GOOD_NUMBERS, event.getGuild().getId())
                .stream()
                .map(Config::getValue)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .toList();
        if (goodNumbers.contains(total)) {
            log.trace("Good numbers in {}: {} totals to {}", FormatLogObject.eventInfo(event), numbers, total);
            countOneStat(componentName, event.getGuild().getId());
            event.getMessage()
                    .reply(buildMessage(numbers, total, LocaleService.getResourceBundleOrDefault(event.getGuild())))
                    .queue();
        }
    }

    private MessageCreateData buildMessage(List<Integer> numbers, int total, ResourceBundle resourceBundle) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.addContent(resourceBundle.getString("listener.goodnumbers.total").formatted(total) + "\n");
        for (int i = 0; i < numbers.size(); i++) {
            var number = numbers.get(i);
            messageCreateBuilder.addContent(number + " ");
            if (i < numbers.size() - 1) {
                messageCreateBuilder.addContent("+ ");
            } else {
                messageCreateBuilder.addContent("= ");
            }
        }
        messageCreateBuilder.addContent("" + total);
        return messageCreateBuilder.build();
    }
}

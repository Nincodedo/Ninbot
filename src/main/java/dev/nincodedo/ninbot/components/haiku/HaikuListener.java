package dev.nincodedo.ninbot.components.haiku;

import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentType;
import dev.nincodedo.ninbot.common.message.MessageUtils;
import dev.nincodedo.ninbot.components.stats.StatManager;
import eu.crydee.syllablecounter.SyllableCounter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Component
public class HaikuListener extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private ConfigService configService;
    private SyllableCounter syllableCounter;
    private Random random;
    private String componentName;

    public HaikuListener(ComponentService componentService, StatManager statManager, ConfigService configService) {
        super(statManager);
        this.componentService = componentService;
        this.configService = configService;
        this.syllableCounter = new SyllableCounter();
        this.random = new SecureRandom();
        this.componentName = "haiku";
        componentService.registerComponent(componentName, ComponentType.ACTION);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()
                || componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        var message = event.getMessage().getContentStripped();
        var guildId = event.getGuild().getId();
        isHaikuable(message, guildId).ifPresent(haikuLines -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.appendDescription(MessageUtils.addSpoilerText(
                    "_" + haikuLines + "_", event.getMessage().getContentRaw()));
            embedBuilder.setFooter("A haiku inspired by " + event.getMember().getEffectiveName());
            event.getChannel()
                    .sendMessage(new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build())
                    .queue(message1 -> countOneStat(componentName, event.getGuild().getId()));
        });
    }

    Optional<String> isHaikuable(String message, String guildId) {
        if (isMessageOnlyCharacters(message) && getSyllableCount(message) == 17) {
            String[] splitMessage = message.split("\\s+");
            List<String> lines = new ArrayList<>();
            var results = checkLine(0, 5, splitMessage);
            if (results.counter() == -1) {
                return Optional.empty();
            } else {
                lines.add(results.line());
            }
            results = checkLine(results.counter(), 7, splitMessage);
            if (results.counter() == -1) {
                return Optional.empty();
            } else {
                lines.add(results.line());
            }
            results = checkLine(results.counter(), 5, splitMessage);
            if (results.counter() == -1) {
                return Optional.empty();
            } else {
                lines.add(results.line());
            }
            if (results.counter() == splitMessage.length && checkChance(guildId)) {
                return Optional.of(lines.get(0) + "\n" + lines.get(1) + "\n" + lines.get(2));
            }
        }
        return Optional.empty();
    }

    private HaikuParsingResults checkLine(int counter, int syllables, String[] splitMessage) {
        StringBuilder line = new StringBuilder();
        for (; counter < splitMessage.length; counter++) {
            String word = splitMessage[counter];
            line.append(word);
            line.append(" ");
            int syllableCount = getSyllableCount(line.toString());
            if (syllableCount > syllables) {
                return new HaikuParsingResults(-1, line.toString());
            } else if (syllableCount == syllables) {
                counter++;
                return new HaikuParsingResults(counter, line.toString());
            }
        }
        return new HaikuParsingResults(-1, line.toString());
    }

    private boolean isMessageOnlyCharacters(String message) {
        return Pattern.compile("^[a-zA-Z\\sé.,!?]+$").matcher(message).matches();
    }

    boolean checkChance(String guildId) {
        var optionalConfig = configService.getGlobalConfigByName(ConfigConstants.HAIKU_CHANCE, guildId);
        int chance = 10;
        if (optionalConfig.isPresent()) {
            chance = Integer.parseInt(optionalConfig.get().getValue());
        }
        return random.nextInt(100) < chance;
    }

    private int getSyllableCount(String message) {
        int count = 0;
        for (String word : message.split("\\s+")) {
            count += syllableCounter.count(word);
        }
        return count;
    }
}

record HaikuParsingResults(int counter, String line) {
}

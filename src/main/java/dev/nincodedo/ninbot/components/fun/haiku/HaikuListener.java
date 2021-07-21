package dev.nincodedo.ninbot.components.fun.haiku;

import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.common.message.MessageUtils;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import eu.crydee.syllablecounter.SyllableCounter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Component
public class HaikuListener extends StatAwareListenerAdapter {

    private ComponentService componentService;
    private SyllableCounter syllableCounter;
    private Random random;
    private String componentName;

    public HaikuListener(ComponentService componentService, StatManager statManager) {
        super(statManager);
        this.componentService = componentService;
        this.syllableCounter = new SyllableCounter();
        this.random = new Random();
        this.componentName = "haiku";
        componentService.registerComponent(componentName, ComponentType.ACTION);
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getAuthor().isBot()
                || componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        val message = event.getMessage().getContentStripped();
        isHaikuable(message).ifPresent(haikuLines -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.appendDescription(MessageUtils.addSpoilerText(
                    "_" + haikuLines + "_", event.getMessage().getContentRaw()));
            embedBuilder.setFooter("A haiku inspired by " + event.getMember().getEffectiveName());
            event.getChannel()
                    .sendMessage(new MessageBuilder(embedBuilder).build())
                    .queue(message1 -> countOneStat(componentName, event.getGuild().getId()));
        });
    }

    Optional<String> isHaikuable(String message) {
        Optional<String> haikuLines = Optional.empty();
        if (isMessageOnlyCharacters(message) && getSyllableCount(message) == 17) {
            StringBuilder firstLine = new StringBuilder();
            String[] split = message.split("\\s+");
            int counter;
            for (counter = 0; counter < split.length; counter++) {
                String word = split[counter];
                firstLine.append(word);
                firstLine.append(" ");
                int syllableCount = getSyllableCount(firstLine.toString());
                if (syllableCount > 5) {
                    return Optional.empty();
                } else if (syllableCount == 5) {
                    counter++;
                    break;
                }
            }
            StringBuilder secondLine = new StringBuilder();
            for (; counter < split.length; counter++) {
                String word = split[counter];
                secondLine.append(word);
                secondLine.append(" ");
                int syllableCount = getSyllableCount(secondLine.toString());
                if (syllableCount > 7) {
                    return Optional.empty();
                } else if (syllableCount == 7) {
                    counter++;
                    break;
                }
            }
            StringBuilder thirdLine = new StringBuilder();
            for (; counter < split.length; counter++) {
                String word = split[counter];
                thirdLine.append(word);
                thirdLine.append(" ");
                int syllableCount = getSyllableCount(thirdLine.toString());
                if (syllableCount > 5) {
                    return Optional.empty();
                } else if (syllableCount == 5 && counter + 1 == split.length) {
                    haikuLines = Optional.of(
                            firstLine + "\n" + secondLine + "\n" + thirdLine);
                    break;
                }
            }
        }
        if (checkChance()) {
            return haikuLines;
        } else {
            return Optional.empty();
        }
    }

    private boolean isMessageOnlyCharacters(String message) {
        return Pattern.compile("^[a-zA-Z\s.,!?]+$").matcher(message).matches();
    }

    boolean checkChance() {
        return random.nextInt(100) < 10;
    }

    private int getSyllableCount(String message) {
        int count = 0;
        for (String word : message.split("\\s+")) {
            count += syllableCounter.count(word);
        }
        return count;
    }
}

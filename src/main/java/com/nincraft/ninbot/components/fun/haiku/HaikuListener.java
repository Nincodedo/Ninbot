package com.nincraft.ninbot.components.fun.haiku;

import com.nincraft.ninbot.components.config.component.ComponentService;
import eu.crydee.syllablecounter.SyllableCounter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HaikuListener extends ListenerAdapter {

    private ComponentService componentService;
    private SyllableCounter syllableCounter;
    private String componentName;

    public HaikuListener(ComponentService componentService) {
        this.componentService = componentService;
        this.syllableCounter = new SyllableCounter();
        this.componentName = "haiku";
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
            embedBuilder.appendDescription("_" + haikuLines + "_");
            embedBuilder.setFooter("A haiku inspired by " + event.getMember().getEffectiveName());
            event.getChannel().sendMessage(new MessageBuilder(embedBuilder).build()).queue();
        });

    }

    Optional<String> isHaikuable(String message) {
        Optional<String> haikuLines = Optional.empty();
        if (getSyllableCount(message) == 17) {
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
                            firstLine.toString() + "\n" + secondLine.toString() + "\n" + thirdLine.toString());
                    break;
                }
            }
        }
        return haikuLines;
    }

    private int getSyllableCount(String message) {
        int count = 0;
        for (String word : message.split("\\s+")) {
            count += syllableCounter.count(word);
        }
        return count;
    }
}

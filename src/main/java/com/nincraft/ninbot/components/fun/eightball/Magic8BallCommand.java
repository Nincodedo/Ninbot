package com.nincraft.ninbot.components.fun.eightball;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Log4j2
@Component
public class Magic8BallCommand extends AbstractCommand {

    private Random random;

    public Magic8BallCommand() {
        name = "8ball";
        aliases = Arrays.asList("magic8ball", "8");
        this.random = new Random();
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<String> answers = readMagic8BallList();
        embedBuilder.setTitle(String.format("%s shakes the Magic 8 Ball", event.getMember().getEffectiveName()));
        embedBuilder.setThumbnail("https://i.imgur.com/80imnZ0.png");
        embedBuilder.appendDescription("The Magic 8 Ball says...\n\n");
        embedBuilder.appendDescription("_" + answers.get(random.nextInt(answers.size())) + "_");
        messageAction.addChannelAction(new MessageBuilder(embedBuilder).build());
        return messageAction;
    }

    private List<String> readMagic8BallList() {
        List<String> answers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                .getResourceAsStream("magic8BallAnswers.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                answers.add(line);
            }
        } catch (IOException e) {
            log.error("Failed to read magic8BallAnswers.txt", e);
        }
        return answers;
    }
}

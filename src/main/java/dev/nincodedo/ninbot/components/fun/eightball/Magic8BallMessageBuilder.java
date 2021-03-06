package dev.nincodedo.ninbot.components.fun.eightball;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Log4j2
public class Magic8BallMessageBuilder {

    private Random random;
    private List<String> eightBallAnswers;

    public Magic8BallMessageBuilder() {
        this.random = new Random();
        this.eightBallAnswers = readMagic8BallList();
    }

    Message getMagic8BallEmbed(String questionAsked, String username) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (StringUtils.isEmpty(StringUtils.trimToEmpty(questionAsked))) {
            embedBuilder.setTitle(String.format("%s shakes the Magic 8 Ball", username));
        } else {
            embedBuilder.setTitle(String.format("%s shakes the Magic 8 Ball and asks \"%s\"", username, questionAsked));
        }
        embedBuilder.setThumbnail("https://i.imgur.com/80imnZ0.png");
        embedBuilder.appendDescription("The Magic 8 Ball says...\n\n");
        embedBuilder.appendDescription("_" + getMagic8BallAnswer() + "_");
        messageBuilder.setEmbeds(embedBuilder.build());
        return messageBuilder.build();
    }

    private String getMagic8BallAnswer() {
        return eightBallAnswers.get(random.nextInt(eightBallAnswers.size()));
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

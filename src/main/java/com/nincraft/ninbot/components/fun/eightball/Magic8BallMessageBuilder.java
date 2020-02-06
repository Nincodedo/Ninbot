package com.nincraft.ninbot.components.fun.eightball;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
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

    public Magic8BallMessageBuilder() {
        this.random = new Random();
    }

    EmbedBuilder getMagic8BallEmbed(String username) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("%s shakes the Magic 8 Ball", username));
        embedBuilder.setThumbnail("https://i.imgur.com/80imnZ0.png");
        embedBuilder.appendDescription("The Magic 8 Ball says...\n\n");
        embedBuilder.appendDescription("_" + getMagic8BallAnswer() + "_");
        return embedBuilder;
    }

    private String getMagic8BallAnswer() {
        List<String> answers = readMagic8BallList();
        return answers.get(random.nextInt(answers.size()));
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

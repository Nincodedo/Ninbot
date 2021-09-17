package dev.nincodedo.ninbot.components.fun.eightball;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class Magic8BallMessageBuilder {

    private Random random;
    private List<String> eightBallAnswers;
    private String question;
    private String memberName;

    public Magic8BallMessageBuilder() {
        this.random = new Random();
        this.eightBallAnswers = readMagic8BallList();
    }

    MessageEmbed build() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (StringUtils.isEmpty(StringUtils.trimToEmpty(question))) {
            embedBuilder.setTitle(String.format("%s shakes the Magic 8 Ball", memberName));
        } else {
            embedBuilder.setTitle(String.format("%s shakes the Magic 8 Ball and asks \"%s\"", memberName, question));
        }
        embedBuilder.setThumbnail("https://i.imgur.com/80imnZ0.png");
        embedBuilder.appendDescription("The Magic 8 Ball says...\n\n");
        embedBuilder.appendDescription("_" + getMagic8BallAnswer() + "_");
        return embedBuilder.build();
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

    public Magic8BallMessageBuilder question(String question) {
        this.question = question;
        return this;
    }

    public Magic8BallMessageBuilder memberName(String effectiveName) {
        this.memberName = effectiveName;
        return this;
    }
}

package dev.nincodedo.ninbot.components.eightball;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class Magic8BallMessageBuilder {

    private Random random;
    private List<String> eightBallAnswers;
    private String question;
    private String userName;

    public Magic8BallMessageBuilder(@Value("classpath:magic8BallAnswers.txt") Resource answerFile) {
        this.random = new SecureRandom();
        this.eightBallAnswers = readMagic8BallList(answerFile);
    }

    MessageEmbed build() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        setEmbedBuilderTitle(embedBuilder);
        embedBuilder.setThumbnail("https://i.imgur.com/80imnZ0.png");
        embedBuilder.appendDescription("The Magic 8 Ball says...\n\n");
        embedBuilder.appendDescription("_" + getMagic8BallAnswer() + "_");
        return embedBuilder.build();
    }

    private void setEmbedBuilderTitle(EmbedBuilder embedBuilder) {
        if (StringUtils.isEmpty(StringUtils.trimToEmpty(question))) {
            embedBuilder.setTitle("%s shakes the Magic 8 Ball".formatted(userName));
        } else {
            embedBuilder.setTitle("%s shakes the Magic 8 Ball and asks \"%s\"".formatted(userName, question));
        }
    }

    private String getMagic8BallAnswer() {
        return eightBallAnswers.get(random.nextInt(eightBallAnswers.size()));
    }

    private List<String> readMagic8BallList(Resource answerFile) {
        List<String> answers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(answerFile.getInputStream()))) {
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

    public Magic8BallMessageBuilder userName(String effectiveName) {
        this.userName = effectiveName;
        return this;
    }

    public MessageEditBuilder reshake(Message message, int maxShakes) {
        var editMessage = MessageEditBuilder.fromMessage(message);
        var embed = editMessage.getEmbeds().getFirst();
        var editEmbed = EmbedBuilder.fromData(embed.toData());
        editEmbed.setDescription(null);
        var footerText = embed.getFooter() != null ? embed.getFooter().getText() : null;
        var shakeCount = getNextShakeCount(maxShakes, footerText);
        editEmbed.setFooter("%s shakes".formatted(shakeCount));
        if (shakeCount >= maxShakes) {
            editMessage.setComponents(ActionRow.of(Button.secondary("empty", "No Shakes Left").asDisabled()));
            editEmbed.appendDescription("After a final shake, the Magic 8 Ball says...\n\n");
        } else {
            editEmbed.appendDescription("After another shake, the Magic 8 Ball says...\n\n");
        }
        editEmbed.appendDescription("_" + getMagic8BallAnswer() + "_");
        editMessage.setEmbeds(editEmbed.build());
        return editMessage;
    }

    protected int getNextShakeCount(int maxShakes, String footerText) {
        int currentShakeCount;
        if (footerText == null) {
            currentShakeCount = 1;
        } else {
            try {
                currentShakeCount = Integer.parseInt(footerText.split(" ")[0]);
            } catch (NumberFormatException e) {
                log.warn("Couldn't get a shake count from footer {}, setting shake count to max.", footerText);
                currentShakeCount = maxShakes;
            }
        }
        return currentShakeCount + 1;
    }
}

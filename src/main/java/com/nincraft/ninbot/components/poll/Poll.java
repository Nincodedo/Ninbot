package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
class Poll {
    private String title;
    private List<String> choices;
    private String result;
    private long timeLength;
    private User user;
    private boolean pollOpen;

    Message build() {
        pollOpen = true;
        return buildPollMessage("Poll will close ");
    }

    Message buildClosed() {
        pollOpen = false;
        return buildPollMessage(result);
    }

    private Message buildPollMessage(String footer) {
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle(title);
        messageBuilder.setColor(getColor(user.getAvatarUrl()));
        if (isPollOpen()) {
            messageBuilder.setTimestamp(Instant.now().plus(timeLength, ChronoUnit.MINUTES));
        } else {
            messageBuilder.setTimestamp(Instant.now());
        }
        messageBuilder.setAuthor("Poll by " + user.getName(), null, user.getAvatarUrl());
        messageBuilder.addField("Choices", buildPollChoices(), false);
        messageBuilder.setFooter(footer, null);
        return messageBuilder.build();
    }

    private Color getColor(String avatarUrl) {
        if (avatarUrl == null) {
            return Color.BLUE;
        } else {
            try {
                URLConnection connection = new URL(avatarUrl).openConnection();
                connection.setRequestProperty("User-Agent", "NING/1.0");
                BufferedImage image = ImageIO.read(connection.getInputStream());
                return getAverageColor(image);
            } catch (IOException e) {
                return Color.BLUE;
            }
        }
    }

    private Color getAverageColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixel = new Color(image.getRGB(x, y));
                redSum += pixel.getRed();
                greenSum += pixel.getGreen();
                blueSum += pixel.getBlue();
            }
        }
        int total = width * height;
        return new Color(redSum / total, greenSum / total, blueSum / total);
    }

    private String buildPollChoices() {
        StringBuilder stringBuilder = new StringBuilder();
        char digitalOneEmoji = '\u0031';
        for (String choice : choices) {
            stringBuilder.append(digitalOneEmoji).append("\u20E3").append(" ").append(choice).append("\n");
            digitalOneEmoji++;
        }
        return stringBuilder.toString();
    }
}

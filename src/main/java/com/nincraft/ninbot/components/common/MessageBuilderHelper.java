package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.temporal.TemporalAccessor;

/**
 * A lazy combo of {@link MessageBuilder} and {@link EmbedBuilder}
 */
public class MessageBuilderHelper {

    private MessageBuilder messageBuilder;
    private EmbedBuilder embedBuilder;

    public MessageBuilderHelper() {
        messageBuilder = new MessageBuilder();
        embedBuilder = new EmbedBuilder();
    }

    /**
     * see {@link EmbedBuilder#setTitle(String)}
     */
    public void setTitle(String title) {
        embedBuilder.setTitle(title);
    }

    /**
     * see {@link EmbedBuilder#setColor(Color)}
     */
    public void setColor(Color color) {
        embedBuilder.setColor(color);
    }

    /**
     * see {@link EmbedBuilder#setTimestamp(TemporalAccessor)}
     */
    public void setTimestamp(TemporalAccessor temporal) {
        embedBuilder.setTimestamp(temporal);
    }

    /**
     * see {@link EmbedBuilder#setAuthor(String, String, String)}
     */
    public void setAuthor(String name, String url, String iconUrl) {
        embedBuilder.setAuthor(name, url, iconUrl);
    }

    /**
     * see {@link EmbedBuilder#setAuthor(String)}
     */
    public void setAuthor(String name) {
        embedBuilder.setAuthor(name);
    }

    /**
     * see {@link EmbedBuilder#addField(String, String, boolean)}
     */
    public void addField(String name, String value, boolean inline) {
        embedBuilder.addField(name, value, inline);
    }

    /**
     * see {@link EmbedBuilder#setFooter(String, String)}
     */
    public void setFooter(String footer, String url) {
        embedBuilder.setFooter(footer, url);
    }

    /**
     * see {@link MessageBuilder#build()}
     */
    public Message build() {
        return messageBuilder.setEmbed(embedBuilder.build()).build();
    }

    /**
     * see {@link EmbedBuilder#appendDescription(CharSequence)}
     */
    public void appendDescription(String description) {
        embedBuilder.appendDescription(description);
    }

    /**
     * Sets the color of the embedded field based on the user's avatar. Defaults to {@link Color#BLUE} if an average color cannot be determined.
     *
     * @param avatarUrl url to the user's avatar
     */
    public void setColor(String avatarUrl) {
        embedBuilder.setColor(getColor(avatarUrl));
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
}

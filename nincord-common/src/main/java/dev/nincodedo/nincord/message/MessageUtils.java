package dev.nincodedo.nincord.message;

import dev.nincodedo.nincord.util.ImageUtils;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

@UtilityClass
public class MessageUtils {
    private static boolean isSpoiler(String message) {
        String checkMessage = message.replaceFirst("\\|\\|", "");
        return checkMessage.contains("||");
    }

    /**
     * Puts spoiler tags around an entire string if the raw message had any spoiler tags.
     *
     * @param message    the content stripped string
     * @param rawMessage the raw content string
     * @return string with spoiler tags around it if the raw content had any spoiler tags in it
     */
    public static String addSpoilerText(String message, String rawMessage) {
        String newMessage = message;
        if (isSpoiler(rawMessage)) {
            newMessage = "||" + newMessage + "||";
        }
        return newMessage;
    }

    /**
     * Returns the average color of a user's avatar.
     *
     * @param avatarUrl url to a user's avatar
     * @return average color of an avatar
     */
    public static Color getColor(String avatarUrl) {
        if (avatarUrl == null) {
            return Color.BLUE;
        } else {
            try {
                URLConnection connection = new URI(avatarUrl).toURL().openConnection();
                connection.setRequestProperty("User-Agent", "NING/1.0");
                BufferedImage image = ImageIO.read(connection.getInputStream());
                return ImageUtils.getAverageColor(image);
            } catch (IOException | URISyntaxException e) {
                return Color.BLUE;
            }
        }
    }
}

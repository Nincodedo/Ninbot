package dev.nincodedo.ninbot.components.common;

import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@UtilityClass
public class MessageBuilderHelper {

    public static Color getColor(String avatarUrl) {
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

    private static Color getAverageColor(BufferedImage image) {
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
        return new Color(redSum / total, greenSum / total, blueSum / total).brighter();
    }
}

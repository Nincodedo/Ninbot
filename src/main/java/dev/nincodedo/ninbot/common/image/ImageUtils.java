package dev.nincodedo.ninbot.common.image;

import lombok.experimental.UtilityClass;

import java.awt.*;
import java.awt.image.BufferedImage;

@UtilityClass
public class ImageUtils {
    /**
     * Gets the average color of an image.
     *
     * @param image the image
     * @return the average color of that image
     */
    public static Color getAverageColor(BufferedImage image) {
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

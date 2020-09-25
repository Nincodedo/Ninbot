package dev.nincodedo.ninbot.components.ac.turnips.generator;

import java.util.Random;

public class RandomUtil {

    private Random random;

    public RandomUtil(Random random) {
        this.random = random;
    }

    public int nextInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public double nextDouble(double min, double max) {
        return (random.nextDouble() * (max - min)) + min;
    }

    public int nextInt(int max) {
        return random.nextInt(max);
    }
}

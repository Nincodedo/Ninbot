package com.nincraft.ninbot.components.ac.turnips.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pattern 3: decreasing, spike, decreasing
 */
public class SmallSpikePriceGenerator extends PatternPriceGenerator {
    @Override
    public List<Integer> generatePrices(int basePrice, Random random) {
        RandomUtil randomUtil = new RandomUtil(random);
        int peakStart = randomUtil.nextInt(2, 10);
        double rate = randomUtil.nextDouble(0.4, 0.9);
        int index;

        List<Integer> prices = new ArrayList<>();

        for (index = 0; index < peakStart; index++) {
            prices.add((int) Math.ceil(rate * basePrice));
            rate -= 0.03;
            rate -= randomUtil.nextDouble(0, 0.02);
        }

        prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        rate = randomUtil.nextDouble(1.4, 2.0);
        prices.add((int) Math.ceil(randomUtil.nextDouble(1.4, rate) * basePrice) - 1);
        prices.add((int) Math.ceil(rate * basePrice));
        prices.add((int) Math.ceil(randomUtil.nextDouble(1.4, rate) * basePrice) - 1);

        index = index + 5;

        if (index < 12) {
            for (; index < 12; index++) {
                prices.add((int) Math.ceil(rate * basePrice));
                rate -= 0.03;
                rate -= randomUtil.nextDouble(0, 0.02);
            }
        }

        return prices;
    }
}

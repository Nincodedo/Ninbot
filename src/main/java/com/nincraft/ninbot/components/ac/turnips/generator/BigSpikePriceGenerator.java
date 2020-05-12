package com.nincraft.ninbot.components.ac.turnips.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pattern 1: decreasing middle, high spike, random low
 */
public class BigSpikePriceGenerator extends PatternPriceGenerator {

    @Override
    public List<Integer> generatePrices(int basePrice, Random random) {
        RandomUtil randomUtil = new RandomUtil(random);
        int index;
        int peakStart = randomUtil.nextInt(3, 10);
        double rate = randomUtil.nextDouble(0.85, 0.9);

        List<Integer> prices = new ArrayList<>();

        for (index = 0; index < peakStart; index++) {
            prices.add((int) Math.ceil(rate * basePrice));
            rate -= 0.03;
            rate -= randomUtil.nextDouble(0, 0.02);
        }

        prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        prices.add((int) Math.ceil(randomUtil.nextDouble(1.4, 2.0) * basePrice));
        prices.add((int) Math.ceil(randomUtil.nextDouble(2.0, 6.0) * basePrice));
        prices.add((int) Math.ceil(randomUtil.nextDouble(1.4, 2.0) * basePrice));
        prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        index = index + 5;
        for (; index < 12; index++) {
            prices.add((int) Math.ceil(randomUtil.nextDouble(0.4, 0.9) * basePrice));
        }
        return prices;
    }
}

package com.nincraft.ninbot.components.ac.turnips.generator;

import java.util.List;
import java.util.Random;

public abstract class PatternPriceGenerator {

    public abstract List<Integer> generatePrices(int basePrice, Random random);

    protected int addDecreasingPrices(int basePrice, RandomUtil randomUtil, int numOfDecreasingPrices, double rate,
            List<Integer> prices) {
        int index;
        for (index = 0; index < numOfDecreasingPrices; index++) {
            prices.add((int) Math.ceil(rate * basePrice));
            rate -= 0.03;
            rate -= randomUtil.nextDouble(0, 0.02);
        }
        return index;
    }
}

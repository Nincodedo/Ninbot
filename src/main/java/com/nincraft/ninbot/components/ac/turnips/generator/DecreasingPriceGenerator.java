package com.nincraft.ninbot.components.ac.turnips.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pattern 2: consistently decreasing
 */
public class DecreasingPriceGenerator extends PatternPriceGenerator {
    @Override
    public List<Integer> generatePrices(int basePrice, Random random) {
        List<Integer> prices = new ArrayList<>();
        RandomUtil randomUtil = new RandomUtil(random);
        double rate = 0.9;
        rate -= randomUtil.nextDouble(0, 0.05);
        addDecreasingPrices(basePrice, randomUtil, 12, rate, prices);
        return prices;
    }
}

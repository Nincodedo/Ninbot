package com.nincraft.ninbot.components.ac.turnips.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pattern 0: high, decreasing, high, decreasing, high
 */
public class RandomPriceGenerator extends PatternPriceGenerator {

    @Override
    public List<Integer> generatePrices(int basePrice, Random random) {

        int decreasingPhaseLength1 = random.nextBoolean() ? 3 : 2;
        int decreasingPhaseLength2 = 5 - decreasingPhaseLength1;
        RandomUtil randomUtil = new RandomUtil(random);
        int highPhaseLength1 = randomUtil.nextInt(0, 7);
        int highPhaseLength2And3 = 7 - highPhaseLength1;
        int highPhaseLength3 = randomUtil.nextInt(highPhaseLength2And3);

        List<Integer> prices = new ArrayList<>();

        //high phase 1
        for (int i = 0; i < highPhaseLength1; i++) {
            prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        }

        //decreasing phase 1
        double rate = randomUtil.nextDouble(0.6, 0.8);
        for (int i = 0; i < decreasingPhaseLength1; i++) {
            prices.add((int) Math.ceil((rate * basePrice)));
            rate -= 0.04;
            rate -= randomUtil.nextDouble(0, 0.06);
        }

        //high phase 2
        for (int i = 0; i < (highPhaseLength2And3 - highPhaseLength3); i++) {
            prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        }

        //decreasing phase 2
        rate = randomUtil.nextDouble(0.6, 0.8);
        for (int i = 0; i < decreasingPhaseLength2; i++) {
            prices.add((int) Math.ceil((rate * basePrice)));
            rate -= 0.04;
            rate -= randomUtil.nextDouble(0, 0.06);
        }

        //high phase 3
        for (int i = 0; i < highPhaseLength3; i++) {
            prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        }
        return prices;
    }
}

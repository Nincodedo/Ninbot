package dev.nincodedo.ninbot.components.ac.turnips.generator;

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
        getHighPhasePrices(basePrice, randomUtil, highPhaseLength1, prices);

        //decreasing phase 1
        double rate = randomUtil.nextDouble(0.6, 0.8);
        getDecreasingPhasePrices(basePrice, decreasingPhaseLength1, randomUtil, prices, rate);

        //high phase 2
        getHighPhasePrices(basePrice, randomUtil, highPhaseLength2And3 - highPhaseLength3, prices);

        //decreasing phase 2
        rate = randomUtil.nextDouble(0.6, 0.8);
        getDecreasingPhasePrices(basePrice, decreasingPhaseLength2, randomUtil, prices, rate);

        //high phase 3
        getHighPhasePrices(basePrice, randomUtil, highPhaseLength3, prices);
        return prices;
    }

    private void getDecreasingPhasePrices(int basePrice, int decreasingPhaseLength1, RandomUtil randomUtil,
            List<Integer> prices, double rate) {
        for (int i = 0; i < decreasingPhaseLength1; i++) {
            prices.add((int) Math.ceil((rate * basePrice)));
            rate -= 0.04;
            rate -= randomUtil.nextDouble(0, 0.06);
        }
    }

    private void getHighPhasePrices(int basePrice, RandomUtil randomUtil, int highPhaseLength1, List<Integer> prices) {
        for (int i = 0; i < highPhaseLength1; i++) {
            prices.add((int) Math.ceil(randomUtil.nextDouble(0.9, 1.4) * basePrice));
        }
    }
}

package com.nincraft.ninbot.components.ac.turnips;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class NewLeafTurnipPriceGenerator extends TurnipPriceGenerator {

    public List<Integer> getRandomTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        List<Integer> turnipPrices = new ArrayList<>();
        Random randomFromSeed = new Random(seed);
        for (int i = 0; i < 12; i++) {
            turnipPrices.add(randomFromSeed.nextInt(turnipPattern.getUpperBound()) + turnipPattern.getBase());
        }
        return turnipPrices;
    }

    public List<Integer> getAllOtherTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        List<Integer> turnipPrices = new ArrayList<>();
        Random randomFromSeed = new Random(seed);
        int spikeDay = 0;
        if (turnipPattern.equals(TurnipPattern.BIG_SPIKE)) {
            spikeDay = new Random(seed).nextInt(6) + 2;
        } else if (turnipPattern.equals(TurnipPattern.SMALL_SPIKE)) {
            spikeDay = new Random(seed).nextInt(7) + 1;
        }
        List<Integer> spikeDays = getSpikeDays(turnipPattern.getSpikeCount(), spikeDay);
        int previous = randomFromSeed.nextInt(turnipPattern.getUpperBound()) + turnipPattern.getBase();
        int previousSpike = 0;
        int maxPosition = 0;
        for (int i = 0; i < 12; i++) {
            if (spikeDays.contains(i)) {
                if (previousSpike == 0) {
                    previousSpike =
                            randomFromSeed.nextInt(turnipPattern.getSpikeUpperBound()) + turnipPattern.getSpikeBase();
                } else {
                    previousSpike = (int) (previousSpike
                            + randomFromSeed.nextInt(turnipPattern.getSpikeUpperBound()) / turnipPattern.getDivisor());
                    maxPosition = i;
                }
                previousSpike = addPriceToList(turnipPrices, previousSpike);
            } else {
                previous = addPriceToList(turnipPrices, previous);
            }
            int nextPrice = previous - (randomFromSeed.nextInt(4) + 2);
            if (nextPrice > 0) {
                previous = nextPrice;
            } else {
                previous = previous / 2;
            }
            if (previous <= 0) {
                previous = 1;
            }
        }

        setDecreasingPostSpikeDays(turnipPattern, turnipPrices, randomFromSeed, maxPosition);

        return turnipPrices;
    }

    @Override
    public TurnipPattern getTurnipPattern(long seed) {
        return TurnipPattern.getRandomTurnipPattern(seed);
    }

    private void setDecreasingPostSpikeDays(TurnipPattern turnipPattern, List<Integer> turnipPrices, Random random,
            int maxPosition) {
        if (turnipPattern.equals(TurnipPattern.BIG_SPIKE) || turnipPattern.equals(TurnipPattern.SMALL_SPIKE)) {
            double average = turnipPrices.stream().mapToInt(value -> value).average().getAsDouble();
            int firstDecreasing =
                    random.nextInt(turnipPrices.get(maxPosition) - turnipPattern.getBase());
            if (firstDecreasing < average) {
                firstDecreasing += average;
            }
            turnipPrices.set(
                    maxPosition + 1, firstDecreasing);
            if (turnipPattern.equals(TurnipPattern.BIG_SPIKE)) {
                turnipPrices.set(maxPosition + 2, firstDecreasing - random.nextInt(turnipPattern.getBase()));
            }
        }
    }
}

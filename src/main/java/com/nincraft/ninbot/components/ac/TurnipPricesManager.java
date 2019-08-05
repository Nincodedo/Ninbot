package com.nincraft.ninbot.components.ac;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TurnipPricesManager {

    private TurnipPricesRepository turnipPricesRepository;

    public TurnipPricesManager(TurnipPricesRepository turnipPricesRepository) {
        this.turnipPricesRepository = turnipPricesRepository;
    }

    public void generateNewWeek() {
        Random random = new Random();
        turnipPricesRepository.deleteAll();
        TurnipPrices turnipPrices = new TurnipPrices();
        turnipPrices.setSeed(random.nextLong());
        turnipPricesRepository.save(turnipPrices);
    }

    public List<Integer> getTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        if (turnipPattern.equals(TurnipPattern.RANDOM)) {
            return getRandomTurnipPricesList(turnipPattern, seed);
        } else {
            return getAllOtherTurnipPricesList(turnipPattern, seed);
        }
    }

    private List<Integer> getRandomTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        List<Integer> turnipPrices = new ArrayList<>();
        Random random = new Random(seed);
        for (int i = 0; i < 12; i++) {
            turnipPrices.add(random.nextInt(turnipPattern.getUpperBound()) + turnipPattern.getBase());
        }
        return turnipPrices;
    }

    private List<Integer> getAllOtherTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        List<Integer> turnipPrices = new ArrayList<>();
        Random random = new Random(seed);
        int spikeDay = 0;
        if (turnipPattern.equals(TurnipPattern.BIG_SPIKE)) {
            spikeDay = random.nextInt(8) + 2;
        } else if (turnipPattern.equals(TurnipPattern.SMALL_SPIKE)) {
            spikeDay = random.nextInt(8) + 1;
        }
        List<Integer> spikeDays = getSpikeDays(turnipPattern.getSpikeCount(), spikeDay);
        int previous = random.nextInt(turnipPattern.getUpperBound()) + turnipPattern.getBase();
        int previousSpike = 0;
        int maxPosition = 0;
        for (int i = 0; i < 12; i++) {
            if (spikeDays.contains(i)) {
                if (previousSpike == 0) {
                    previousSpike = random.nextInt(turnipPattern.getSpikeUpperBound()) + turnipPattern.getSpikeBase();
                } else {
                    previousSpike = (int) (previousSpike
                            + random.nextInt(turnipPattern.getSpikeUpperBound()) / turnipPattern.getDivisor());
                    maxPosition = i;
                }
                turnipPrices.add(previousSpike);
            } else {
                turnipPrices.add(previous);
            }
            previous = previous - (random.nextInt(5) + 2);
        }


        setDecreasingPostSpikeDays(turnipPattern, turnipPrices, random, maxPosition);

        return turnipPrices;
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

    private List<Integer> getSpikeDays(int spikeCount, int spikeDay) {
        List<Integer> spikeDays = new ArrayList<>();
        for (int i = spikeDay; i < spikeCount + spikeDay; i++) {
            spikeDays.add(i);
        }
        return spikeDays;
    }

    int getSundayTurnipPrices(long seed) {
        Random random = new Random(seed);
        return random.nextInt(20) + 90;
    }
}

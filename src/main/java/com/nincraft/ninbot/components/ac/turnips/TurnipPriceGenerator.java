package com.nincraft.ninbot.components.ac.turnips;

import java.util.ArrayList;
import java.util.List;

public abstract class TurnipPriceGenerator {


    abstract List<Integer> getRandomTurnipPricesList(TurnipPattern turnipPattern, long seed);

    abstract List<Integer> getAllOtherTurnipPricesList(TurnipPattern turnipPattern, long seed);

    int addPriceToList(List<Integer> turnipPrices, int price) {
        if (price <= 0) {
            price = 1;
        }
        turnipPrices.add(price);
        return price;
    }

    List<Integer> getSpikeDays(int spikeCount, int spikeDay) {
        List<Integer> spikeDays = new ArrayList<>();
        for (int i = spikeDay; i < spikeCount + spikeDay; i++) {
            spikeDays.add(i);
        }
        return spikeDays;
    }

    public abstract TurnipPattern getTurnipPattern(long seed);

    public List<Integer> getTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        if (turnipPattern.equals(TurnipPattern.RANDOM)) {
            return getRandomTurnipPricesList(turnipPattern, seed);
        } else {
            return getAllOtherTurnipPricesList(turnipPattern, seed);
        }
    }
}

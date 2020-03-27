package com.nincraft.ninbot.components.ac.turnips;

import java.util.List;

public class NewHorizonsTurnipPriceGenerator extends TurnipPriceGenerator{
    @Override
    List<Integer> getRandomTurnipPricesList(
            TurnipPattern turnipPattern, long seed) {
        return null;
    }

    @Override
    List<Integer> getAllOtherTurnipPricesList(TurnipPattern turnipPattern, long seed) {
        return null;
    }

    @Override
    public TurnipPattern getTurnipPattern(long seed) {
        return null;
    }
}

package com.nincraft.ninbot.components.ac.turnips;

import java.util.Random;

public enum TurnipPattern {
    DECREASING, BIG_SPIKE, SMALL_SPIKE, RANDOM;

    public static TurnipPattern getRandomTurnipPattern(long seed) {
        Random random = new Random(seed);
        return values()[random.nextInt(values().length)];
    }
}

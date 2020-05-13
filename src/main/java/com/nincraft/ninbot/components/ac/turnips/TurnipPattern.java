package com.nincraft.ninbot.components.ac.turnips;

import lombok.Getter;

import java.util.Random;

@Getter
public enum TurnipPattern {
    DECREASING, BIG_SPIKE, SMALL_SPIKE, RANDOM;

    TurnipPattern() {
    }

    public static TurnipPattern getRandomTurnipPattern(long seed) {
        Random random = new Random(seed);
        return values()[random.nextInt(values().length)];
    }
}

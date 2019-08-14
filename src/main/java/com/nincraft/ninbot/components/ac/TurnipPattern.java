package com.nincraft.ninbot.components.ac;

import lombok.Getter;

import java.util.Random;

@Getter
public enum TurnipPattern {
    DECREASING, BIG_SPIKE(3, 100, 400, 1.5), SMALL_SPIKE(4, 65, 175, 5), RANDOM(150, 50);

    private int spikeUpperBound;
    private int spikeBase;
    private int spikeCount;
    private int upperBound = 49;
    private int base = 50;
    private double divisor;

    TurnipPattern() {
    }

    TurnipPattern(int upperBound, int base) {
        this.upperBound = upperBound;
        this.base = base;
    }

    TurnipPattern(int spikeCount, int spikeBase, int spikeUpperBound, double divisor) {
        this.spikeCount = spikeCount;
        this.spikeBase = spikeBase;
        this.spikeUpperBound = spikeUpperBound;
        this.divisor = divisor;
    }

    public static TurnipPattern getRandomTurnipPattern(long seed) {
        Random random = new Random(seed);
        return values()[random.nextInt(values().length)];
    }
}

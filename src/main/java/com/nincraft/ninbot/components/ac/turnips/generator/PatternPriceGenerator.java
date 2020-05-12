package com.nincraft.ninbot.components.ac.turnips.generator;

import java.util.List;
import java.util.Random;

public abstract class PatternPriceGenerator {

    public abstract List<Integer> generatePrices(int basePrice, Random random);
}

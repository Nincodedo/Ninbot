package com.nincraft.ninbot.components.ac.turnips;

import lombok.val;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TurnipPricesService {

    private TurnipPricesRepository turnipPricesRepository;

    public TurnipPricesService(TurnipPricesRepository turnipPricesRepository) {
        this.turnipPricesRepository = turnipPricesRepository;
    }

    public TurnipPattern getPreviousPattern(long seed) {
        val lastWeek = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        val twoWeeksAgo = LocalDateTime.now().minus(14, ChronoUnit.DAYS);
        val optionalPrice = turnipPricesRepository.findFirstByCreatedBetween(lastWeek, twoWeeksAgo);
        //if there's a previous week for the turnips, then return that pattern, otherwise generate one based on the seed
        if (optionalPrice.isPresent()) {
            return TurnipPattern.getRandomTurnipPattern(optionalPrice.get().getSeed());
        } else {
            return TurnipPattern.getRandomTurnipPattern(seed);
        }
    }
}

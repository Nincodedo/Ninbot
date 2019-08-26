package com.nincraft.ninbot.components.ac.turnips;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TurnipPricesRepository extends CrudRepository<TurnipPrices, Long> {
    List<TurnipPrices> findAll();
}

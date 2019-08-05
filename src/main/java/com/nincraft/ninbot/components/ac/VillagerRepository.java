package com.nincraft.ninbot.components.ac;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VillagerRepository extends CrudRepository<Villager, Long> {
    Optional<Villager> findByDiscordId(String discordId);
}

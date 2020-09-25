package dev.nincodedo.ninbot.components.ac;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface VillagerRepository extends PagingAndSortingRepository<Villager, Long> {
    Optional<Villager> findByDiscordId(String discordId);

    List<Villager> findAll();
}

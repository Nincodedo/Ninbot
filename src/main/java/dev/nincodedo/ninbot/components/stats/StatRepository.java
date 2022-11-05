package dev.nincodedo.ninbot.components.stats;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StatRepository extends CrudRepository<Stat, Long> {
    Optional<Stat> findByNameAndCategoryAndServerId(String name, String category, String serverId);
}

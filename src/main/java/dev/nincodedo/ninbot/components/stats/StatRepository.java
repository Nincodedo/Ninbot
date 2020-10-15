package dev.nincodedo.ninbot.components.stats;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StatRepository extends CrudRepository<Stat, Long> {
    Optional<Stat> findByName(String name);
    List<Stat> findByCategory(String category);
    List<Stat> findByServerId(String serverId);
    List<Stat> findByCategoryAndServerId(String category, String serverId);
}

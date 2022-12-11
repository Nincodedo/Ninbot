package dev.nincodedo.nincord.stats;

import dev.nincodedo.nincord.persistence.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatRepository extends BaseRepository<Stat> {
    Optional<Stat> findByNameAndCategoryAndServerId(String name, String category, String serverId);
}

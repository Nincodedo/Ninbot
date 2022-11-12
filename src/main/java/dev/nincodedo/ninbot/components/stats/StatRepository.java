package dev.nincodedo.ninbot.components.stats;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface StatRepository extends BaseRepository<Stat> {
    Optional<Stat> findByNameAndCategoryAndServerId(String name, String category, String serverId);

}

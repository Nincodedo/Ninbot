package dev.nincodedo.ninbot.components.stats;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface StatRepository extends BaseRepository<Stat> {

    default Optional<Stat> findByNameAndCategoryAndServerId(String name, String category, String serverId) {
        return findByNameAndCategoryAndServerIdAndDeleted(name, category, serverId, false);
    }

    Optional<Stat> findByNameAndCategoryAndServerIdAndDeleted(String name, String category, String serverId,
            Boolean isDeleted);
}

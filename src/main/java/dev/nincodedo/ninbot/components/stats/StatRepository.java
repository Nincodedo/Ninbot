package dev.nincodedo.ninbot.components.stats;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface StatRepository extends BaseRepository<Stat> {

    default Optional<Stat> findByNameAndCategoryAndServerId(String name, String category, String serverId) {
        return findByNameAndCategoryAndServerIdAndDeleted(name, category, serverId, false);
    }

    default List<Stat> findByCategory(String category) {
        return findByCategoryAndDeleted(category, false);
    }

    default List<Stat> findByServerId(String serverId) {
        return findByServerIdAndDeleted(serverId, false);
    }

    default List<Stat> findByCategoryAndServerId(String category, String serverId) {
        return findByCategoryAndServerIdAndDeleted(category, serverId, false);
    }

    Optional<Stat> findByNameAndCategoryAndServerIdAndDeleted(String name, String category, String serverId,
            Boolean isDeleted);

    List<Stat> findByCategoryAndDeleted(String category, Boolean isDeleted);

    List<Stat> findByServerIdAndDeleted(String serverId, Boolean isDeleted);

    List<Stat> findByCategoryAndServerIdAndDeleted(String category, String serverId, Boolean isDeleted);
}

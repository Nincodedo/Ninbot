package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.List;

public interface ComponentConfigurationRepository extends BaseRepository<ComponentConfiguration> {
    List<ComponentConfiguration> findByEntityIdAndEntityType(String entityId, DiscordEntityType entityType);

    List<ComponentConfiguration> findByComponentAndEntityIdAndEntityType(Component component, String entityId,
            DiscordEntityType entityType);

    default List<ComponentConfiguration> findByServerId(String serverId) {
        return findByEntityIdAndEntityType(serverId, DiscordEntityType.SERVER);
    }

    default List<ComponentConfiguration> findByUserId(String userId) {
        return findByEntityIdAndEntityType(userId, DiscordEntityType.USER);
    }

    default List<ComponentConfiguration> findByComponentAndServerId(Component component, String serverId) {
        return findByComponentAndEntityIdAndEntityType(component, serverId, DiscordEntityType.SERVER);
    }

    default List<ComponentConfiguration> findByComponentAndUserId(Component component, String userId) {
        return findByComponentAndEntityIdAndEntityType(component, userId, DiscordEntityType.USER);
    }

    List<ComponentConfiguration> findByComponent(Component component);
}

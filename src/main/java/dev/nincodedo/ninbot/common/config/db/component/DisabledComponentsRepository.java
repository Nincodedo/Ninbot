package dev.nincodedo.ninbot.common.config.db.component;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;

public interface DisabledComponentsRepository extends BaseRepository<DisabledComponents> {
    default List<DisabledComponents> findByComponentAndServerId(Component component, String serverId) {
        return findByComponentAndServerIdAndDeleted(component, serverId, false);
    }

    default List<DisabledComponents> findByServerId(String serverId) {
        return findByServerIdAndDeleted(serverId, false);
    }

    List<DisabledComponents> findByServerIdAndDeleted(String serverId, Boolean isDeleted);

    List<DisabledComponents> findByComponentAndServerIdAndDeleted(Component component, String serverId,
            Boolean isDeleted);
}

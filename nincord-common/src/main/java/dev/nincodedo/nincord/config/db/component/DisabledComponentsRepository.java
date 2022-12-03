package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.List;

public interface DisabledComponentsRepository extends BaseRepository<DisabledComponents> {

    List<DisabledComponents> findByServerId(String serverId);

    List<DisabledComponents> findByComponentAndServerId(Component component, String serverId);
}

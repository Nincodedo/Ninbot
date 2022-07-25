package dev.nincodedo.ninbot.common.config.db.component;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisabledComponentsRepository extends CrudRepository<DisabledComponents, Long> {
    List<DisabledComponents> findByComponentAndServerId(Component component, String serverId);

    List<DisabledComponents> findByServerId(String serverId);
}

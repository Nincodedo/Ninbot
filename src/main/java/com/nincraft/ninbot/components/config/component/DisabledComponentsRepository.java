package com.nincraft.ninbot.components.config.component;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisabledComponentsRepository extends CrudRepository<DisabledComponents, Long> {
    List<DisabledComponents> findByComponentAndServerId(Component component, String serverId);

    List<DisabledComponents> findByServerId(String serverId);
}

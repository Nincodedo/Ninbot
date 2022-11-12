package dev.nincodedo.ninbot.common.config.db.component;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface ComponentRepository extends BaseRepository<Component> {

    Component findByName(String name);

    Optional<Component> findByNameAndType(String name, ComponentType componentType);
}

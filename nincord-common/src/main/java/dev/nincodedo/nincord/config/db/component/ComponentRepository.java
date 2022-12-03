package dev.nincodedo.nincord.config.db.component;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.Optional;

public interface ComponentRepository extends BaseRepository<Component> {

    Component findByName(String name);

    Optional<Component> findByNameAndType(String name, ComponentType componentType);
}

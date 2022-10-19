package dev.nincodedo.ninbot.common.config.db.component;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.Optional;

public interface ComponentRepository extends BaseRepository<Component> {
    default Component findByName(String name) {
        return findByNameAndDeleted(name, false);
    }

    default Optional<Component> findByNameAndType(String name, ComponentType componentType) {
        return findByNameAndTypeAndDeleted(name, componentType, false);
    }

    Component findByNameAndDeleted(String name, Boolean isDeleted);

    Optional<Component> findByNameAndTypeAndDeleted(String name, ComponentType componentType, Boolean isDeleted);

}

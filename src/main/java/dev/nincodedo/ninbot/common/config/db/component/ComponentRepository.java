package dev.nincodedo.ninbot.common.config.db.component;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends CrudRepository<Component, Long> {
    @NotNull List<Component> findAll();

    Component findByName(String name);

    Optional<Component> findByNameAndType(String name, ComponentType componentType);
}

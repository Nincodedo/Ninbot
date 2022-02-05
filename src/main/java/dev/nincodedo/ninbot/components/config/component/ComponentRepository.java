package dev.nincodedo.ninbot.components.config.component;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends CrudRepository<Component, Long> {
    @NotNull List<Component> findAll();

    Component findByName(String name);

    Optional<Component> findByNameAndType(String name, ComponentType componentType);
}

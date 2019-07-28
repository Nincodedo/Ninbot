package com.nincraft.ninbot.components.config.component;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentRepository extends CrudRepository<Component, Long> {
    List<Component> findAll();

    Component findByName(String name);

    Optional<Component> findByNameAndType(String name, ComponentType componentType);
}

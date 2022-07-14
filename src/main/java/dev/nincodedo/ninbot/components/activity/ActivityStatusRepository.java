package dev.nincodedo.ninbot.components.activity;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityStatusRepository extends CrudRepository<ActivityStatus, Long> {
    @NotNull List<ActivityStatus> findAll();
}

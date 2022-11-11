package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;

public interface Scheduler<T extends BaseEntity, R extends BaseRepository<T>> {
    List<T> findAllOpenItems();

    default void save(T item) {
        getRepository().save(item);
    }

    R getRepository();
}

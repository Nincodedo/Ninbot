package dev.nincodedo.nincord;

import dev.nincodedo.nincord.persistence.BaseEntity;
import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.List;

public interface Scheduler<T extends BaseEntity, R extends BaseRepository<T>> {
    List<T> findAllOpenItems();

    default void save(T item) {
        getRepository().save(item);
    }

    R getRepository();
}

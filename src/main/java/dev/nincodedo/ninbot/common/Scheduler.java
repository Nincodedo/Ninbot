package dev.nincodedo.ninbot.common;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface Scheduler<T, R extends CrudRepository<T, Long>> {
    List<T> findAllOpenItems();

    default void save(T item) {
        getRepository().save(item);
    }

    R getRepository();
}

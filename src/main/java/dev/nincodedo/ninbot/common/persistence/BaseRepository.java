package dev.nincodedo.ninbot.common.persistence;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    @NotNull List<T> findAllByDeleted(Boolean isDeleted);

    @Override
    @NotNull
    default List<T> findAll() {
        return findAllByDeleted(false);
    }

    @Override
    default void delete(T entity) {
        entity.setDeleted(true);
        this.save(entity);
    }
}

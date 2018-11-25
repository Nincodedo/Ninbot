package com.nincraft.ninbot.components.common;

import lombok.val;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

public class GenericDao<T> {
    private final Class<T> generic;
    protected EntityManager entityManager;

    public GenericDao(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.generic = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), GenericDao.class);
    }

    @Transactional
    public List<T> getAllObjectsByServerId(String serverId) {
        val query = entityManager.createQuery("FROM " + generic.getName() + " where serverId = :serverId", generic);
        query.setParameter("serverId", serverId);
        return query.getResultList();

    }

    @Transactional
    public void saveObject(T object) {
        entityManager.persist(object);
    }

    @Transactional
    public void removeObject(T object) {
        entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
    }

    @Transactional
    public List<T> getAllObjects() {
        return entityManager.createQuery("FROM " + generic.getName(), generic).getResultList();
    }
}

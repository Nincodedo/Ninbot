package com.nincraft.ninbot.components.common;

import lombok.val;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityManager;
import java.util.List;

public class GenericDao<T> {
    private final Class<T> generic;
    protected EntityManager entityManager;

    public GenericDao(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.generic = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), GenericDao.class);
    }

    public List<T> getAllObjectsByServerId(String serverId) {
        val query = entityManager.createQuery("FROM " + generic.getName() + " where serverId = :serverId", generic);
        query.setParameter("serverId", serverId);
        return query.getResultList();

    }

    public void saveObject(T object) {
        entityManager.persist(object);
    }

    public void removeObject(T object) {
        entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
    }

    public List<T> getAllObjects() {
        return entityManager.createQuery("FROM " + generic.getName(), generic).getResultList();
    }
}

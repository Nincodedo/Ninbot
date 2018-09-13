package com.nincraft.ninbot.components.common;

import lombok.val;
import org.hibernate.SessionFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenericDao<T> {
    private final Class<T> generic;
    protected SessionFactory sessionFactory;

    public GenericDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.generic = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), GenericDao.class);
    }

    public List<T> getAllObjectsByServerId(String serverId) {
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM " + generic.getName() + " where serverId = :serverId", generic);
            query.setParameter("serverId", serverId);
            return query.getResultList();
        }
    }

    public void saveObject(T object) {
        try (val session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
        }
    }

    public void removeObject(T object) {
        try (val session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(session.contains(object) ? object : session.merge(object));
            session.getTransaction().commit();
        }
    }

    public List<T> getAllObjects() {
        try (val session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + generic.getName(), generic).getResultList();
        }
    }
}

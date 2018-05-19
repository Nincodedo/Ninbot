package com.nincraft.ninbot.components.config;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class ConfigDao {
    private SessionFactory sessionFactory;

    @Autowired
    public ConfigDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    List<Config> getConfigByName(String serverId, String configName) {
        log.info("Getting configs for {} {}", serverId, configName);
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM Config where serverId = :serverId and key = :configName", Config.class);
            query.setParameter("serverId", serverId);
            query.setParameter("configName", configName);
            return query.getResultList();
        }
    }

    List<String> getValuesByName(String serverId, String configName) {
        log.info("Getting config values for {} {}", serverId, configName);
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM Config where serverId = :serverId and key = :configName", Config.class);
            query.setParameter("serverId", serverId);
            query.setParameter("configName", configName);
            return query.getResultList().stream().map(Config::getValue).collect(Collectors.toList());
        }
    }

    void removeConfig(String serverId, String configName, String configValue) {
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("DELETE Config WHERE serverId = :serverId and key = :configName and value = :configValue");
            query.setParameter("serverId", serverId);
            query.setParameter("configName", configName);
            query.setParameter("configValue", configValue);
            query.executeUpdate();
        }
    }

    boolean addConfig(String serverId, String configName, String configValue) {
        Config config = new Config(serverId, configName, configValue);
        try (val session = sessionFactory.openSession()) {
            session.persist(config);
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.error("Failed to set config {} with value {} for server {}", configName, configValue, serverId);
            log.error(e);
            return false;
        }
        return true;
    }
}

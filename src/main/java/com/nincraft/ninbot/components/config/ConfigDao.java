package com.nincraft.ninbot.components.config;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Repository
@Transactional
public class ConfigDao extends GenericDao<Config> {
    private static final String CONFIG_NAME = "configName";
    private static final String SERVER_ID = "serverId";

    @Autowired
    public ConfigDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    List<Config> getConfigByName(String serverId, String configName) {
        log.info("Getting configs for {} {}", serverId, configName);
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM Config where serverId = :serverId and key = :configName", Config.class);
            query.setParameter(SERVER_ID, serverId);
            query.setParameter(CONFIG_NAME, configName);
            return query.getResultList();
        }
    }

    List<String> getValuesByName(String serverId, String configName) {
        log.info("Getting config values for {} {}", serverId, configName);
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM Config where serverId = :serverId and key = :configName", Config.class);
            query.setParameter(SERVER_ID, serverId);
            query.setParameter(CONFIG_NAME, configName);
            return query.getResultList().stream().map(Config::getValue).collect(Collectors.toList());
        }
    }

    void removeConfig(String serverId, String configName, String configValue) {
        try (val session = sessionFactory.openSession()) {
            session.beginTransaction();
            val list = getConfigByName(serverId, configName);
            list.stream().filter(config -> config.getValue().equals(configValue)).forEach(config ->
                    session.delete(session.contains(config) ? config : session.merge(config)));
            session.getTransaction().commit();
        }
    }

    boolean addConfig(String serverId, String configName, String configValue) {
        Config config = new Config(serverId, configName, configValue);
        try (val session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(config);
            transaction.commit();
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.error("Failed to set config {} with value {} for server {}", configName, configValue, serverId);
            log.error(e);
            return false;
        }
        return true;
    }
}

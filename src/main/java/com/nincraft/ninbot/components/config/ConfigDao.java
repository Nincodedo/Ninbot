package com.nincraft.ninbot.components.config;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Transactional;
import java.util.Optional;

@Log4j2
@Repository
public class ConfigDao {
    private final EntityManager entityManager;

    @Autowired
    public ConfigDao(@Qualifier("entityManagerFactoryBean") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Config> getConfigByName(String serverId, String configName) {
        val query = entityManager.createQuery("FROM Config where serverId = :serverId and key = :configName", Config.class);
        query.setParameter("serverId", serverId);
        query.setParameter("configName", configName);
        return Optional.ofNullable(query.getResultList().get(0));
    }

    @Transactional
    public boolean setConfig(String serverId, String configName, String configValue) {
        Config config = new Config(serverId, configName, configValue);
        try {
            entityManager.persist(config);
        } catch (EntityExistsException | TransactionRequiredException e) {
            log.error("Failed to set config {} with value {} for server {}", configName, configValue, serverId);
            log.error(e);
            return false;
        }
        return true;
    }
}

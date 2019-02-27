package com.nincraft.ninbot.components.config;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Repository
@Transactional
public class ConfigDao extends GenericDao<Config> {
    private static final String CONFIG_NAME = "configName";
    private static final String SERVER_ID = "serverId";

    @Autowired
    public ConfigDao(EntityManager entityManager) {
        super(entityManager);
    }

    List<Config> getConfigByName(String serverId, String configName) {
        log.debug("Getting configs for {} {}", serverId, configName);
        val query = entityManager.createQuery("FROM Config where serverId = :serverId and name = :configName", Config.class);
        query.setParameter(SERVER_ID, serverId);
        query.setParameter(CONFIG_NAME, configName);
        return query.getResultList();
    }

    List<String> getValuesByName(String serverId, String configName) {
        log.debug("Getting config values for {} {}", serverId, configName);
        val query = entityManager.createQuery("FROM Config where serverId = :serverId and name = :configName", Config.class);
        query.setParameter(SERVER_ID, serverId);
        query.setParameter(CONFIG_NAME, configName);
        return query.getResultList().stream().map(Config::getValue).collect(Collectors.toList());
    }

    void removeConfig(String serverId, String configName, String configValue) {
        val list = getConfigByName(serverId, configName);
        list.stream().filter(config -> config.getValue().equals(configValue)).forEach(config ->
                entityManager.remove(entityManager.contains(config) ? config : entityManager.merge(config)));
    }

    void addConfig(String serverId, String configName, String configValue) {
        Config config = new Config(serverId, configName, configValue);
        entityManager.persist(config);
    }

    void removeConfig(Config config) {
        entityManager.remove(entityManager.contains(config) ? config : entityManager.merge(config));
    }

    public boolean addConfig(Config config) {
        entityManager.persist(config);
        return true;
    }
}

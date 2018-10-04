package com.nincraft.ninbot.components.config;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {

    private ConfigDao configDao;

    public ConfigService(ConfigDao configDao) {
        this.configDao = configDao;
    }

    @Transactional
    public List<Config> getConfigByName(String serverId, String configName) {
        return configDao.getConfigByName(serverId, configName);
    }

    @Transactional
    public List<String> getValuesByName(java.lang.String serverId, java.lang.String configName) {
        return configDao.getValuesByName(serverId, configName);
    }

    public Optional<String> getSingleValueByName(String serverId, String configName) {
        return Optional.ofNullable(getValuesByName(serverId, configName).get(0));
    }

    @Transactional
    public void removeConfig(Config config) {
        configDao.removeConfig(config);
    }

    @Transactional
    public void removeConfig(String serverId, String configName, String configValue) {
        configDao.removeConfig(serverId, configName, configValue);
    }

    @Transactional
    public boolean addConfig(Config config) {
        return configDao.addConfig(config);
    }

    @Transactional
    public boolean addConfig(String serverId, String configName, String configValue) {
        return configDao.addConfig(serverId, configName, configValue);
    }

    @Transactional
    public List<Config> getConfigsByServerId(String serverId) {
        return configDao.getAllObjectsByServerId(serverId);
    }
}

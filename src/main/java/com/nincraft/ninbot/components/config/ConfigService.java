package com.nincraft.ninbot.components.config;

import lombok.val;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfigService {

    private ConfigDao configDao;
    private Map<String, List<Config>> cachedConfigs = new HashMap<>();

    public ConfigService(ConfigDao configDao) {
        this.configDao = configDao;
    }

    @Transactional
    public List<Config> getConfigByName(String serverId, String configName) {
        if (cachedConfigs.containsKey(serverId)) {
            return cachedConfigs.get(serverId);
        }
        val listOfConfigs = configDao.getConfigByName(serverId, configName);
        cachedConfigs.put(serverId, listOfConfigs);
        return listOfConfigs;
    }

    @Transactional
    public List<String> getValuesByName(String serverId, String configName) {
        return configDao.getValuesByName(serverId, configName);
    }

    @Transactional
    public Optional<String> getSingleValueByName(String serverId, String configName) {
        val list = configDao.getValuesByName(serverId, configName);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(list.get(0));
        }
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
    public void addConfig(String serverId, String configName, String configValue) {
        configDao.addConfig(serverId, configName, configValue);
    }

    @Transactional
    public List<Config> getConfigsByServerId(String serverId) {
        return configDao.getAllObjectsByServerId(serverId);
    }
}

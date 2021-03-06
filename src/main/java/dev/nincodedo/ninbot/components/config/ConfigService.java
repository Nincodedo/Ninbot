package dev.nincodedo.ninbot.components.config;

import lombok.val;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigService {

    private ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Transactional
    @Cacheable("configs-by-name")
    public List<Config> getConfigByName(String serverId, String configName) {
        return configRepository.getConfigsByServerIdAndName(serverId, configName);
    }

    @Transactional
    @Cacheable("config-values-by-name")
    public List<String> getValuesByName(String serverId, String configName) {
        val list = configRepository.getConfigsByServerIdAndName(serverId, configName);
        return list.stream().map(Config::getValue).collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public Optional<String> getSingleValueByName(String serverId, String configName) {
        val list = configRepository.getConfigsByServerIdAndName(serverId, configName);
        val valueList = list.stream().map(Config::getValue).collect(Collectors.toCollection(ArrayList::new));
        if (valueList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(valueList.get(0));
        }
    }

    @Transactional
    public Optional<Config> getConfigByServerIdAndName(String serverId, String configName) {
        return configRepository.getConfigByServerIdAndName(serverId, configName);
    }

    @Transactional
    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name"})
    public void removeConfig(Config config) {
        configRepository.delete(config);
    }

    @Transactional
    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name"})
    public void removeConfig(String serverId, String configName, String configValue) {
        configRepository.getConfigByServerIdAndNameAndValue(serverId, configName, configValue).ifPresent(config ->
                configRepository.delete(config)
        );
    }

    @Transactional
    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name"})
    public void addConfig(Config config) {
        configRepository.save(config);
    }

    @Transactional
    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name"})
    public void addConfig(String serverId, String configName, String configValue) {
        configRepository.save(new Config(serverId, configName, configValue));
    }

    @Transactional
    public List<Config> getConfigsByServerId(String serverId) {
        return configRepository.getConfigsByServerId(serverId);
    }

    @Transactional
    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name"})
    public void updateConfig(Config config) {
        configRepository.getConfigByServerIdAndName(config.getServerId(), config.getName()).ifPresent(oldConfig -> {
            oldConfig.setValue(config.getValue());
            configRepository.save(oldConfig);
        });
    }

    @Transactional
    @Cacheable("global-configs-by-name")
    public Optional<Config> getGlobalConfigByName(String configName, String serverId) {
        //First try to find config by server ID. If none found, then use the global
        val serverConfig = configRepository.getConfigsByServerIdAndName(serverId, configName);
        if (serverConfig.isEmpty()) {
            return configRepository.getConfigByNameAndGlobal(configName, true);
        } else {
            return Optional.of(serverConfig.get(0));
        }
    }

    @Transactional
    public boolean isConfigEnabled(String configName, String serverId, String configValue) {
        return configRepository.getConfigByServerIdAndNameAndValue(serverId, configName, configValue).isPresent();
    }
}

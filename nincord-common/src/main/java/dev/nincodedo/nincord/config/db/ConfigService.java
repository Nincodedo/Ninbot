package dev.nincodedo.nincord.config.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ConfigService {

    private ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Cacheable("configs-by-name")
    public List<Config> getConfigByName(String serverId, String configName) {
        return configRepository.getConfigsByServerIdAndName(serverId, configName);
    }

    @Cacheable("config-values-by-name")
    public List<String> getValuesByName(String serverId, String configName) {
        var list = configRepository.getConfigsByServerIdAndName(serverId, configName);
        return list.stream().map(Config::getValue).collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<String> getSingleValueByName(String serverId, String configName) {
        var list = configRepository.getConfigsByServerIdAndName(serverId, configName);
        var valueList = list.stream().map(Config::getValue).collect(Collectors.toCollection(ArrayList::new));
        if (valueList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(valueList.get(0));
        }
    }

    public Optional<Config> getConfigByServerIdAndName(String serverId, String configName) {
        return configRepository.getConfigByServerIdAndName(serverId, configName);
    }

    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name", "global-configs-by-name"})
    public void removeConfig(Config config) {
        configRepository.delete(config);
    }

    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name", "global-configs-by-name"})
    public void removeConfig(String serverId, String configName, String configValue) {
        configRepository.getConfigByServerIdAndNameAndValue(serverId, configName, configValue).ifPresent(config ->
                configRepository.delete(config)
        );
    }

    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name", "global-configs-by-name"})
    public void addConfig(Config config) {
        configRepository.save(config);
    }

    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name", "global-configs-by-name"})
    public void addConfig(String serverId, String configName, String configValue) {
        configRepository.save(new Config(serverId, configName, configValue));
    }

    public List<Config> getConfigsByServerId(String serverId) {
        return configRepository.getConfigsByServerId(serverId);
    }

    @CacheEvict(allEntries = true, value = {"configs-by-name", "config-values-by-name", "global-configs-by-name"})
    public void updateConfig(Config config) {
        configRepository.getConfigByServerIdAndName(config.getServerId(), config.getName()).ifPresent(oldConfig -> {
            oldConfig.setValue(config.getValue());
            configRepository.save(oldConfig);
        });
    }

    @Cacheable("global-configs-by-name")
    public List<Config> getGlobalConfigsByName(String configName, String serverId) {
        //First try to find config by server ID. If none found, then use the global
        var serverConfig = configRepository.getConfigsByServerIdAndName(serverId, configName);
        if (serverConfig.isEmpty()) {
            return configRepository.getConfigsByNameAndGlobal(configName, true);
        } else {
            return serverConfig;
        }
    }

    @Cacheable("global-configs-by-name")
    public Optional<Config> getGlobalConfigByName(String configName, String serverId) {
        //First try to find config by server ID. If none found, then use the global
        var serverConfig = configRepository.getConfigsByServerIdAndName(serverId, configName);
        if (serverConfig.isEmpty()) {
            return configRepository.getConfigByNameAndGlobal(configName, true);
        } else {
            return Optional.of(serverConfig.get(0));
        }
    }

    public boolean isConfigEnabled(String configName, String serverId, String configValue) {
        return configRepository.getConfigByServerIdAndNameAndValue(serverId, configName, configValue).isPresent();
    }
}

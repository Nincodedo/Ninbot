package com.nincraft.ninbot.components.config;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConfigRepository extends CrudRepository<Config, Long> {
    List<Config> getConfigsByServerIdAndName(String serverId, String name);

    List<Config> getConfigsByServerId(String serverId);

    Optional<Config> getConfigByServerIdAndName(String serverId, String name);

    Optional<Config> getConfigByServerIdAndNameAndValue(String serverId, String name, String value);

    Optional<Config> getConfigByNameAndGlobal(String name, Boolean isGlobal);
}

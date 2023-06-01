package dev.nincodedo.nincord.config.db;

import dev.nincodedo.nincord.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface ConfigRepository extends BaseRepository<Config> {

    List<Config> getConfigsByServerId(String serverId);

    Optional<Config> getConfigByServerIdAndName(String serverId, String name);

    List<Config> getConfigsByServerIdAndName(String serverId, String name);

    Optional<Config> getConfigByServerIdAndNameAndValue(String serverId, String name, String value);

    List<Config> getConfigsByNameAndGlobal(String name, Boolean isGlobal);

    Optional<Config> getConfigByNameAndGlobal(String name, Boolean isGlobal);
}

package dev.nincodedo.ninbot.common.config.db;

import dev.nincodedo.ninbot.common.persistence.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface ConfigRepository extends BaseRepository<Config> {

    default List<Config> getConfigsByServerIdAndName(String serverId, String name) {
        return getConfigsByServerIdAndNameAndDeleted(serverId, name, false);
    }

    default List<Config> getConfigsByServerId(String serverId) {
        return getConfigsByServerIdAndDeleted(serverId, false);
    }

    default Optional<Config> getConfigByServerIdAndName(String serverId, String name) {
        return getConfigByServerIdAndNameAndDeleted(serverId, name, false);
    }

    default Optional<Config> getConfigByServerIdAndNameAndValue(String serverId, String name, String value) {
        return getConfigByServerIdAndNameAndValueAndDeleted(serverId, name, value, false);
    }

    default Optional<Config> getConfigByNameAndGlobal(String name, Boolean isGlobal) {
        return getConfigByNameAndGlobalAndDeleted(name, isGlobal, false);
    }

    List<Config> getConfigsByServerIdAndNameAndDeleted(String serverId, String name, Boolean isDeleted);

    List<Config> getConfigsByServerIdAndDeleted(String serverId, Boolean isDeleted);

    Optional<Config> getConfigByServerIdAndNameAndDeleted(String serverId, String name, Boolean isDeleted);

    Optional<Config> getConfigByServerIdAndNameAndValueAndDeleted(String serverId, String name, String value,
            Boolean isDeleted);

    Optional<Config> getConfigByNameAndGlobalAndDeleted(String name, Boolean isGlobal, Boolean isDeleted);
}

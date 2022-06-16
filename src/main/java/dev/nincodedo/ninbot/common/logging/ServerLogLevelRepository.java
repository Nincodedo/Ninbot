package dev.nincodedo.ninbot.common.logging;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ServerLogLevelRepository extends CrudRepository<ServerLogLevel, Long> {
    Optional<ServerLogLevel> findByServerId(String serverId);
}

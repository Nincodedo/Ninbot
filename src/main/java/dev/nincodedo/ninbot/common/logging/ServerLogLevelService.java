package dev.nincodedo.ninbot.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerLogLevelService {
    private ServerLogLevelRepository serverLogLevelRepository;

    ServerLogLevelService(ServerLogLevelRepository serverLogLevelRepository) {
        this.serverLogLevelRepository = serverLogLevelRepository;
    }

    public boolean isTraceEnabled(String serverId) {
        return isTraceEnabled(getLogLevel(serverId));
    }

    public boolean isTraceEnabled(LogLevel logLevel) {
        return logLevel == LogLevel.TRACE;
    }

    public boolean isDebugEnabled(String serverId) {
        var logLevel = getLogLevel(serverId);
        return isDebugEnabled(logLevel);
    }

    public boolean isDebugEnabled(LogLevel logLevel) {
        return logLevel == LogLevel.DEBUG || isTraceEnabled(logLevel);
    }

    public boolean isInfoEnabled(String serverId) {
        var logLevel = getLogLevel(serverId);
        return isInfoEnabled(logLevel);
    }

    public boolean isInfoEnabled(LogLevel logLevel) {
        return logLevel == LogLevel.INFO || isDebugEnabled(logLevel);
    }

    public boolean isWarnEnabled(String serverId) {
        var logLevel = getLogLevel(serverId);
        return isWarnEnabled(logLevel);
    }

    public boolean isWarnEnabled(LogLevel logLevel) {
        return logLevel == LogLevel.WARN || isInfoEnabled(logLevel);
    }

    public boolean isErrorEnabled(String serverId) {
        var logLevel = getLogLevel(serverId);
        return isErrorEnabled(logLevel);
    }

    public boolean isErrorEnabled(LogLevel logLevel) {
        return logLevel == LogLevel.ERROR || isWarnEnabled(logLevel);
    }

    @Cacheable("server-id-log-level")
    public LogLevel getLogLevel(String serverId) {
        var serverLogLevelOptional = serverLogLevelRepository.findByServerId(serverId);
        return serverLogLevelOptional.map(ServerLogLevel::getLogLevel).orElse(getAppLogLevel());
    }

    private LogLevel getAppLogLevel() {
        if (log.isTraceEnabled()) {
            return LogLevel.TRACE;
        } else if (log.isDebugEnabled()) {
            return LogLevel.DEBUG;
        } else if (log.isInfoEnabled()) {
            return LogLevel.INFO;
        } else if (log.isWarnEnabled()) {
            return LogLevel.WARN;
        } else if (log.isErrorEnabled()) {
            return LogLevel.ERROR;
        }
        return LogLevel.OFF;
    }

    @CacheEvict(allEntries = true, value = {"server-id-log-level"})
    public void setLogLevel(String serverId, LogLevel logLevel) {
        var serverLogLevel = serverLogLevelRepository.findByServerId(serverId)
                .orElse(new ServerLogLevel().setServerId(serverId));
        serverLogLevel.setLogLevel(logLevel);
        serverLogLevelRepository.save(serverLogLevel);
    }
}

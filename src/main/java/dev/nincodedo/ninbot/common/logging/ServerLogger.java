package dev.nincodedo.ninbot.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerLogger {
    private ServerLogLevelService serverLogLevelService;

    public ServerLogger(ServerLogLevelService serverLogLevelService) {
        this.serverLogLevelService = serverLogLevelService;
    }

    public void trace(String serverId, String message) {
        if (serverLogLevelService.isTraceEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.trace(serverMarker, message);
        }
    }

    public void trace(String serverId, String format, Object... argArray) {
        if (serverLogLevelService.isTraceEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.trace(serverMarker, format, argArray);
        }
    }

    public void debug(String serverId, String message) {
        if (serverLogLevelService.isDebugEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.debug(serverMarker, message);
        }
    }

    public void info(String serverId, String message) {
        if (serverLogLevelService.isInfoEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.info(serverMarker, message);
        }
    }

    public void warn(String serverId, String message) {
        if (serverLogLevelService.isWarnEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.warn(serverMarker, message);
        }
    }

    public void error(String serverId, String message) {
        if (serverLogLevelService.isErrorEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.error(serverMarker, message);
        }
    }

    public void error(String serverId, Throwable exception, String format, Object... argArray) {
        if (serverLogLevelService.isErrorEnabled(serverId)) {
            Marker serverMarker = MarkerFactory.getMarker(serverId);
            log.error(serverMarker, ParameterizedMessage.format(format, argArray), exception);
        }
    }
}

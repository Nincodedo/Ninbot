package dev.nincodedo.ninbot.common.logging;

import org.springframework.stereotype.Component;

@Component
public class ServerLoggerFactory {
    private ServerLogLevelService serverLogLevelService;

    public ServerLoggerFactory(ServerLogLevelService serverLogLevelService) {
        this.serverLogLevelService = serverLogLevelService;
    }

    public ServerLogger getLogger(Class clazz) {
        return new ServerLogger(serverLogLevelService, clazz);
    }
}

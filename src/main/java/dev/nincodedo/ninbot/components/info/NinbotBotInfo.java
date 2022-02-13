package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.NinbotConstants;
import dev.nincodedo.ninbot.common.command.BotInfo;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

@Component
public class NinbotBotInfo extends BotInfo {
    protected NinbotBotInfo(GitProperties gitProperties, MetricsEndpoint metricsEndpoint) {
        super(gitProperties, metricsEndpoint);
    }

    @Override
    public String getGitHubUrl() {
        return NinbotConstants.NINBOT_GITHUB_URL;
    }

    @Override
    public String getSupporterServerId() {
        return NinbotConstants.NINBOT_SUPPORTERS_SERVER_ID;
    }

    @Override
    public String getDocumentationUrl() {
        return NinbotConstants.NINBOT_DOCUMENTATION_URL;
    }
}

package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.nincord.command.slash.info.BotInfo;
import dev.nincodedo.nincord.config.properties.NincordProperties;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

@Component
public class NinbotBotInfo extends BotInfo {

    private NincordProperties autoConfig;

    protected NinbotBotInfo(GitProperties gitProperties, MetricsEndpoint metricsEndpoint,
            NincordProperties autoConfig) {
        super(gitProperties, metricsEndpoint);
        this.autoConfig = autoConfig;
    }

    @Override
    public String getGitHubUrl() {
        return autoConfig.info().githubUrl();
    }

    @Override
    public String getSupporterGuildId() {
        return autoConfig.supporter().patreonServerId();
    }

    @Override
    public String getDocumentationUrl() {
        return autoConfig.info().documentationUrl();
    }
}

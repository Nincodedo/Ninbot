package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.nincord.command.slash.info.BotInfo;
import dev.nincodedo.nincord.config.properties.InfoCommandConfig;
import dev.nincodedo.nincord.config.properties.SupporterConfig;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({InfoCommandConfig.class, SupporterConfig.class})
public class NinbotBotInfo extends BotInfo {

    protected NinbotBotInfo(GitProperties gitProperties, MetricsEndpoint metricsEndpoint,
            InfoCommandConfig infoCommandConfig, SupporterConfig supporterConfig) {
        super(gitProperties, metricsEndpoint, infoCommandConfig, supporterConfig);
    }
}

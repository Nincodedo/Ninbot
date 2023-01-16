package dev.nincodedo.nincord.command.slash.info;

import dev.nincodedo.nincord.config.properties.InfoCommandConfig;
import dev.nincodedo.nincord.config.properties.SupporterConfig;
import lombok.Data;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.GitProperties;

import java.time.Instant;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Data
public abstract class BotInfo {

    private final MetricsEndpoint metricsEndpoint;
    private final String commitHash;
    private final Instant timeStarted;
    private final InfoCommandConfig infoCommandConfig;
    private final SupporterConfig supporterConfig;

    protected BotInfo(GitProperties gitProperties, MetricsEndpoint metricsEndpoint,
            InfoCommandConfig infoCommandConfig, SupporterConfig supporterConfig) {
        this.commitHash = gitProperties.getCommitId();
        this.metricsEndpoint = metricsEndpoint;
        this.timeStarted = Instant.now();
        this.infoCommandConfig = infoCommandConfig;
        this.supporterConfig = supporterConfig;
    }

    String getUptime(ResourceBundle resourceBundle) {
        var uptimeMilliseconds = TimeUnit.SECONDS.toMillis(metricsEndpoint.metric("process.uptime", null)
                .getMeasurements()
                .get(0)
                .getValue()
                .longValue());
        return getDurationString(resourceBundle, uptimeMilliseconds);
    }

    private String getDurationString(ResourceBundle resourceBundle, long uptimeMilliseconds) {
        String duration = "";
        var durationString = DurationFormatUtils.formatDuration(uptimeMilliseconds, "dd:HH:mm:ss", false).split(":");
        if (!"0".equals(durationString[3])) {
            duration += durationString[3] + " " + resourceBundle.getString("command.info.seconds");
        }
        if (!"0".equals(durationString[2])) {
            duration = durationString[2] + " " + resourceBundle.getString("command.info.minutes") + " " + duration;
        }
        if (!"0".equals(durationString[1])) {
            duration = durationString[1] + " " + resourceBundle.getString("command.info.hours") + " " + duration;
        }
        if (!"0".equals(durationString[0])) {
            duration = durationString[0] + " " + resourceBundle.getString("command.info.days") + " " + duration;
        }
        return duration;
    }

    public String getGitHubUrl() {
        return infoCommandConfig.githubUrl();
    }

    public String getSupporterGuildId() {
        return supporterConfig.patreonServerId();
    }

    public String getDocumentationUrl() {
        return infoCommandConfig.documentationUrl();
    }
}

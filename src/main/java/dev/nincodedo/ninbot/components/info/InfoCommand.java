package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.Constants;
import dev.nincodedo.ninbot.components.common.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Component
public class InfoCommand extends AbstractCommand {

    private GitProperties gitProperties;
    private MetricsEndpoint metricsEndpoint;

    public InfoCommand(GitProperties gitProperties, MetricsEndpoint metricsEndpoint) {
        name = "info";
        length = 2;
        this.gitProperties = gitProperties;
        this.metricsEndpoint = metricsEndpoint;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.info.title"));
        embedBuilder.addField(resourceBundle.getString("command.info.git.hash"), gitProperties.getCommitId(), false);
        val uptime = metricsEndpoint.metric("process.uptime", null);
        val uptimeMilliseconds = TimeUnit.SECONDS.toMillis(uptime.getMeasurements().get(0).getValue().longValue());
        embedBuilder.addField(resourceBundle.getString("command.info.uptime"), getDurationString(resourceBundle,
                uptimeMilliseconds), false);
        embedBuilder.addField(resourceBundle.getString("command.info.githublink.name"),
                String.format(resourceBundle.getString("command.info.githublink.value"), Constants.NINBOT_GITHUB_URL),
                false);
        embedBuilder.addField(resourceBundle.getString("command.info.githublink.issues"),
                Constants.NINBOT_GITHUB_URL + "/issues/new/choose", false);
        embedBuilder.addField(resourceBundle.getString("command.info.documentation.name"),
                Constants.NINBOT_DOCUMENTATION_URL, false);
        messageAction.addChannelAction(new MessageBuilder(embedBuilder).build());
        return messageAction;
    }

    private String getDurationString(ResourceBundle resourceBundle, long uptimeMilliseconds) {
        String duration = "";
        val durationString = DurationFormatUtils.formatDuration(uptimeMilliseconds, "dd:HH:mm:ss", false).split(":");
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
}

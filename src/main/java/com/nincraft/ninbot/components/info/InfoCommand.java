package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.info.title"));
        embedBuilder.addField(resourceBundle.getString("command.info.git.hash"), gitProperties.getCommitId(), false);
        val uptime = metricsEndpoint.metric("process.uptime", null);
        val uptimeMilliseconds = TimeUnit.SECONDS.toMillis(uptime.getMeasurements().get(0).getValue().longValue());
        embedBuilder.addField(resourceBundle.getString("command.info.uptime"), getDurationString(uptimeMilliseconds), false);
        commandResult.addChannelAction(new MessageBuilder(embedBuilder).build());
        return commandResult;
    }

    private String getDurationString(long uptimeMilliseconds) {
        String duration = "";
        val durationString = DurationFormatUtils.formatDuration(uptimeMilliseconds, "dd:HH:mm:ss", false).split(":");
        if (!"0".equals(durationString[3])) {
            duration += durationString[3] + " seconds";
        }
        if (!"0".equals(durationString[2])) {
            duration = durationString[2] + " minutes " + duration;
        }
        if (!"0".equals(durationString[1])) {
            duration = durationString[1] + " hours " + duration;
        }
        if (!"0".equals(durationString[0])) {
            duration = durationString[0] + " days " + duration;
        }
        return duration;
    }
}

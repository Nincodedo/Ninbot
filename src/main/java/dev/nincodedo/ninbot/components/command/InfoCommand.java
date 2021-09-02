package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.common.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class InfoCommand implements SlashCommand {

    private GitProperties gitProperties;
    private MetricsEndpoint metricsEndpoint;
    private Instant timeStarted;

    public InfoCommand(GitProperties gitProperties, MetricsEndpoint metricsEndpoint) {
        this.gitProperties = gitProperties;
        this.metricsEndpoint = metricsEndpoint;
        this.timeStarted = Instant.now();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(resourceBundle().getString("command.info.title"), Constants.NINBOT_GITHUB_URL,
                slashCommandEvent.getJDA()
                        .getSelfUser()
                        .getEffectiveAvatarUrl());
        var extra = slashCommandEvent.getOption("extra");
        if (extra != null && extra.getAsBoolean()) {
            var uptime = metricsEndpoint.metric("process.uptime", null);
            var uptimeMilliseconds = TimeUnit.SECONDS.toMillis(uptime.getMeasurements().get(0).getValue().longValue());
            embedBuilder.addField(resourceBundle().getString("command.info.git.hash"), gitProperties.getCommitId(),
                            false)
                    .addField(resourceBundle().getString("command.info.uptime"), getDurationString(resourceBundle(),
                            uptimeMilliseconds), false)
                    .setFooter(resourceBundle().getString("command.info.startedAt"))
                    .setTimestamp(timeStarted);
        }
        embedBuilder.addField(resourceBundle().getString("command.info.githublink.name"),
                        String.format(resourceBundle().getString("command.info.githublink.value"),
                                Constants.NINBOT_GITHUB_URL),
                        false)
                .addField(resourceBundle().getString("command.info.githublink.issues"),
                        Constants.NINBOT_GITHUB_URL + "/issues/new/choose", false)
                .addField(resourceBundle().getString("command.info.documentation.name"),
                        Constants.NINBOT_DOCUMENTATION_URL, false);
        var patronsList = getPatronsList(slashCommandEvent.getJDA().getShardManager());
        if (patronsList != null && !patronsList.isEmpty()) {
            embedBuilder.addField(resourceBundle().getString("command.info.patreonthanks.name"), patronsList, false);
        }
        slashCommandEvent.reply(new MessageBuilder(embedBuilder).build()).queue();
    }

    private String getPatronsList(ShardManager shardManager) {
        var ninbotPatronServer = shardManager.getGuildById(Constants.NINBOT_SUPPORTERS_SERVER_ID);
        if (ninbotPatronServer != null) {
            return ninbotPatronServer
                    .getMembersWithRoles(Collections.emptyList())
                    .stream()
                    .map(Member::getUser)
                    .filter(user -> !user.isBot())
                    .map(User::getName)
                    //get out of here ya freeloader
                    .filter(userName -> !userName.equalsIgnoreCase("nincodedo"))
                    .map(username -> username + "|||")
                    .collect(Collectors.joining())
                    .trim()
                    .replace("|||", ", ");
        }
        return null;
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

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.BOOLEAN, "extra", "Return additional details."));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }
}

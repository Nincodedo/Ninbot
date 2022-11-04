package dev.nincodedo.ninbot.common.command.slash.info;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InfoCommand implements SlashCommand {

    private BotInfo botInfo;

    protected InfoCommand(BotInfo botInfo) {
        this.botInfo = botInfo;
    }

    @Override
    public MessageExecutor execute(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        var selfUser = slashCommandEvent.getJDA().getSelfUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(selfUser.getName(), botInfo.getGitHubUrl(), selfUser.getEffectiveAvatarUrl());
        var extra = slashCommandEvent.getOption(InfoCommandName.Option.EXTRA.get(), OptionMapping::getAsBoolean);
        if (Boolean.TRUE.equals(extra)) {
            embedBuilder.addField(resourceBundle().getString("command.info.git.hash"), botInfo.getCommitHash(), false)
                    .addField(resourceBundle().getString("command.info.uptime"), botInfo.getUptime(resourceBundle()),
                            false)
                    .setFooter(resourceBundle().getString("command.info.startedAt"))
                    .setTimestamp(botInfo.getTimeStarted());
        }
        embedBuilder.addField(resourceBundle().getString("command.info.githublink.name"),
                        String.format(resourceBundle().getString("command.info.githublink.value"),
                                botInfo.getGitHubUrl()), false)
                .addField(resourceBundle().getString("command.info.githublink.issues"),
                        botInfo.getGitHubUrl() + "/issues/new/choose", false)
                .addField(resourceBundle().getString("command.info.documentation.name"),
                        botInfo.getDocumentationUrl(), false);
        var patronsList = getPatronsList(slashCommandEvent.getJDA().getShardManager());
        if (patronsList != null && !patronsList.isEmpty()) {
            embedBuilder.addField(resourceBundle().getString("command.info.patreonthanks.name"), patronsList, false);
        }
        messageExecutor.addMessageResponse(new MessageCreateBuilder().addEmbeds(embedBuilder.build()).build());
        return messageExecutor;
    }

    public String getPatronsList(ShardManager shardManager) {
        if (shardManager == null) {
            return null;
        }
        var supporterServer = shardManager.getGuildById(botInfo.getSupporterGuildId());
        if (supporterServer != null) {
            return supporterServer.getMembersWithRoles(Collections.emptyList())
                    .stream()
                    .filter(member -> !member.isOwner())
                    .map(Member::getUser)
                    .filter(user -> !user.isBot())
                    .map(user -> user.getName() + user.getDiscriminator())
                    .collect(Collectors.joining(", "))
                    .trim();
        }
        return null;
    }

    @Override
    public String getName() {
        return InfoCommandName.INFO.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.BOOLEAN, InfoCommandName.Option.EXTRA.get(), "Return additional "
                + "details."));
    }
}

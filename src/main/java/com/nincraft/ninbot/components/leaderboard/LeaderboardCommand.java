package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class LeaderboardCommand extends AbstractCommand {

    private LeaderboardService leaderboardService;
    private ConfigService configService;

    public LeaderboardCommand(LeaderboardService leaderboardService, ConfigService configService) {
        name = "leaderboard";
        length = 2;
        checkExactLength = false;
        description = "Displays this server's leaderboard";
        this.leaderboardService = leaderboardService;
        this.configService = configService;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "":
                displayLeaderboard(event);
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void displayLeaderboard(MessageReceivedEvent event) {
        val serverId = event.getGuild().getId();
        val list = leaderboardService.getAllEntriesForServer(serverId);
        list.sort(Comparator.comparingInt(LeaderboardEntry::getWins));
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        val leaderboardName = configService.getSingleValueByName(serverId, ConfigConstants.LEADERBOARD_NAME);
        if (leaderboardName.isPresent()) {
            embedBuilder.setTitle(leaderboardName.get());
        } else {
            embedBuilder.setTitle("Leaderboard");
        }
        for (val entry : list) {
            val user = event.getGuild().getMemberById(entry.getUserId());
            embedBuilder.addField(user.getEffectiveName(), entry.getRecord(), false);
        }
        messageBuilder.setEmbed(embedBuilder.build());
        messageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }
}

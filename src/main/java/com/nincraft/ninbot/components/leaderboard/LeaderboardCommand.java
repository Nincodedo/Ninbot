package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

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
    Optional<CommandResult> val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "":
                displayLeaderboard(event);
                break;
            case "admin":
                if (userHasPermission(event.getGuild(), event.getAuthor(), RolePermission.ADMIN)) {
                    adminSubCommandParse(event);
                } else {
                    messageUtils.reactUnsuccessfulResponse(event.getMessage());
                }
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void adminSubCommandParse(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val splitMessage = message.split("\\s+");
        if (splitMessage.length == 4) {
            runAdminSubCommand(splitMessage[3], event);
        } else {
            messageUtils.reactUnknownResponse(event.getMessage());
        }
    }

    private void runAdminSubCommand(String command, MessageReceivedEvent event) {
        switch (command) {
            case "clear":
                clearLeaderboard(event);
                messageUtils.reactSuccessfulResponse(event.getMessage());
                break;
            default:
                messageUtils.reactUnknownResponse(event.getMessage());
                break;
        }
    }

    private void clearLeaderboard(MessageReceivedEvent event) {
        leaderboardService.removeAllEntriesForServer(event.getGuild().getId());
    }

    private void displayLeaderboard(MessageReceivedEvent event) {
        val serverId = event.getGuild().getId();
        val list = leaderboardService.getAllEntriesForServer(serverId);
        list.sort(Comparator.comparingInt(LeaderboardEntry::getWins));
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        val leaderboardOptional = configService.getSingleValueByName(serverId, ConfigConstants.LEADERBOARD_NAME);
        if (leaderboardOptional.isPresent()) {
            messageBuilder.setTitle(leaderboardOptional.get());
        } else {
            messageBuilder.setTitle("Leaderboard");
        }
        for (val entry : list) {
            val user = event.getGuild().getMemberById(entry.getUserId());
            messageBuilder.addField(user.getEffectiveName(), entry.getRecord(), false);
        }
        messageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }
}

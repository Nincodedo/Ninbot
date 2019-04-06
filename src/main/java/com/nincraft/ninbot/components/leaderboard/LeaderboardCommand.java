package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
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
        this.leaderboardService = leaderboardService;
        this.configService = configService;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "":
                commandResult.addChannelAction(displayLeaderboard(event));
                break;
            case "admin":
                if (userHasPermission(event.getGuild(), event.getAuthor(), RolePermission.ADMIN)) {
                    adminSubCommandParse(event, commandResult);
                } else {
                    commandResult.addUnsuccessfulReaction();
                }
                break;
            default:
                commandResult.addUnknownReaction();
                break;
        }
        return commandResult;
    }

    private void adminSubCommandParse(MessageReceivedEvent event,
            CommandResult commandResult) {
        val message = event.getMessage().getContentStripped();
        val splitMessage = message.split("\\s+");
        if (splitMessage.length == 4) {
            runAdminSubCommand(splitMessage[3], event, commandResult);
        } else {
            commandResult.addUnknownReaction();
        }
    }

    private void runAdminSubCommand(String command, MessageReceivedEvent event,
            CommandResult commandResult) {
        switch (command) {
            case "clear":
                clearLeaderboard(event);
                commandResult.addSuccessfulReaction();
                break;
            default:
                commandResult.addUnknownReaction();
                break;
        }
    }

    private void clearLeaderboard(MessageReceivedEvent event) {
        leaderboardService.removeAllEntriesForServer(event.getGuild().getId());
    }

    private Message displayLeaderboard(MessageReceivedEvent event) {
        val serverId = event.getGuild().getId();
        val list = leaderboardService.getAllEntriesForServer(serverId);
        list.sort(Comparator.comparingInt(LeaderboardEntry::getWins));
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        val leaderboardOptional = configService.getSingleValueByName(serverId, ConfigConstants.LEADERBOARD_NAME);
        messageBuilder.setTitle(leaderboardOptional.orElse(resourceBundle.getString("command.leaderboard.display.defaulttitle")));
        for (val entry : list) {
            entry.setResourceBundle(resourceBundle);
            val user = event.getGuild().getMemberById(entry.getUserId());
            messageBuilder.addField(user.getEffectiveName(), entry.getRecord(), false);
        }
        return messageBuilder.build();
    }
}

package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class LeaderboardCommand extends AbstractCommand {

    private LeaderboardService leaderboardService;

    public LeaderboardCommand(LeaderboardService leaderboardService) {
        name = "leaderboard";
        length = 2;
        checkExactLength = false;
        this.leaderboardService = leaderboardService;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "":
                messageAction.addChannelAction(displayLeaderboard(event));
                break;
            case "admin":
                if (userHasPermission(event.getGuild(), event.getAuthor(), RolePermission.ADMIN)) {
                    adminSubCommandParse(event, messageAction);
                } else {
                    messageAction.addUnsuccessfulReaction();
                }
                break;
            default:
                messageAction.addUnknownReaction();
                break;
        }
        return messageAction;
    }

    private void adminSubCommandParse(MessageReceivedEvent event,
            MessageAction messageAction) {
        val message = event.getMessage().getContentStripped();
        val splitMessage = message.split("\\s+");
        if (splitMessage.length == 4) {
            runAdminSubCommand(splitMessage[3], event, messageAction);
        } else {
            messageAction.addUnknownReaction();
        }
    }

    private void runAdminSubCommand(String command, MessageReceivedEvent event,
            MessageAction messageAction) {
        switch (command) {
            case "clear" -> {
                clearLeaderboard(event);
                messageAction.addSuccessfulReaction();
            }
            default -> messageAction.addUnknownReaction();
        }
    }

    private void clearLeaderboard(MessageReceivedEvent event) {
        leaderboardService.removeAllEntriesForServer(event.getGuild().getId());
    }

    private Message displayLeaderboard(MessageReceivedEvent event) {
        val serverId = event.getGuild().getId();
        val list = leaderboardService.getAllEntriesForServer(serverId);
        list.sort(Comparator.comparingInt(LeaderboardEntry::getWins));
        EmbedBuilder embedBuilder = new EmbedBuilder();
        val leaderboardOptional = configService.getSingleValueByName(serverId, ConfigConstants.LEADERBOARD_NAME);
        embedBuilder.setTitle(leaderboardOptional.orElse(
                resourceBundle.getString("command.leaderboard.display.defaulttitle")));
        for (val entry : list) {
            entry.setResourceBundle(resourceBundle);
            val user = event.getGuild().getMemberById(entry.getUserId());
            embedBuilder.addField(user.getEffectiveName(), entry.getRecord(), false);
        }
        return new MessageBuilder(embedBuilder).build();
    }
}

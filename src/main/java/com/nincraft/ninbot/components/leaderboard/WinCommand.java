package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import lombok.val;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WinCommand extends AbstractCommand {

    private LeaderboardService leaderboardService;

    public WinCommand(LeaderboardService leaderboardService) {
        name = "win";
        length = 3;
        helpText = "win @Username";
        aliases = Arrays.asList("won", "beat", "smashed", "owned");
        this.leaderboardService = leaderboardService;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val mentionedUsers = event.getMessage().getMentionedUsers();
        List<User> mentionList = mentionedUsers.stream().filter(user -> !user.isBot()).collect(Collectors.toList());
        mentionList.remove(event.getAuthor());
        if (!mentionList.isEmpty()) {
            val message = event.getMessage();
            commandResult.addSuccessfulReaction();
            commandResult.addUnsuccessfulReaction();
            mentionList.forEach(user -> {
                val jda = event.getJDA();
                val reactionResultListener = new ReactionResultListener(leaderboardService, name, message.getId(), event
                        .getAuthor()
                        .getId(), user.getId());
                jda.addEventListener(reactionResultListener);
            });
        } else {
            commandResult.addUnknownReaction();
        }
        return commandResult;
    }
}

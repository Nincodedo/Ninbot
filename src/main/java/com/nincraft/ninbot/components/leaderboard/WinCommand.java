package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.command.AbstractCommand;
import lombok.val;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WinCommand extends AbstractCommand {

    private LeaderboardService leaderboardService;

    public WinCommand(LeaderboardService leaderboardService) {
        name = "win";
        length = 3;
        description = "Report a " + name + " against another user";
        helpText = name + " @Username";
        this.leaderboardService = leaderboardService;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        val mentionedUsers = event.getMessage().getMentionedUsers();
        List<User> mentionList = mentionedUsers.stream().filter(user -> !user.isBot()).collect(Collectors.toList());
        if (!mentionList.isEmpty()) {
            val message = event.getMessage();
            messageUtils.reactSuccessfulResponse(message);
            messageUtils.reactUnsuccessfulResponse(message);
            for (val user : mentionList) {
                val jda = event.getJDA();
                val reactionResultListener = new ReactionResultListener(leaderboardService, name, message.getId(), event.getAuthor().getId(), user.getId());
                jda.addEventListener(reactionResultListener);
            }
        }
    }
}

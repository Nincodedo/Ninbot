package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.trivia.game.TriviaManager;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class TriviaCommand extends AbstractCommand {

    private TriviaManager triviaManager;
    private TriviaScoreService triviaScoreService;

    public TriviaCommand(TriviaManager triviaManager, TriviaScoreService triviaScoreService) {
        name = "trivia";
        length = 3;
        checkExactLength = false;
        helpText = "Sub commands\n"
                + "start [ID] - starts trivia in a channel with an optional category ID\n"
                + "stop - stop trivia in a channel\n"
                + "categories - list available trivia categories\n"
                + "score - shows your current score\n"
                + "leaderboard - shows the top 5 players";
        this.triviaManager = triviaManager;
        this.triviaScoreService = triviaScoreService;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "start":
                startTrivia(event, commandResult);
                break;
            case "stop":
                stopTrivia(event, commandResult);
                break;
            case "categories":
                commandResult.addChannelAction(displayTriviaCategories());
                break;
            case "score":
                commandResult.addChannelAction(getPlayerScore(event));
                break;
            case "leaderboard":
                displayLeaderboard(event).ifPresent(commandResult::addChannelAction);
                break;
            default:
                commandResult.addUnsuccessfulReaction();
                break;
        }
        return commandResult;
    }

    private Optional<Message> displayLeaderboard(MessageReceivedEvent event) {
        List<TriviaScore> triviaScores = triviaScoreService.getScoreForAllPlayers();
        if (triviaScores.isEmpty()) {
            return Optional.empty();
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trivia Leaderboard");
        Collections.sort(triviaScores);
        for (int i = 0; i < triviaScores.size() && i < 5; i++) {
            TriviaScore triviaScore = triviaScores.get(i);
            val member = event.getGuild().getMemberById(triviaScore.getUserId());
            if (member != null) {
                embedBuilder.addField(member.getEffectiveName(), Integer.toString(triviaScore.getScore()), false);
            }
        }
        return Optional.of(new MessageBuilder(embedBuilder).build());
    }

    private Message getPlayerScore(MessageReceivedEvent event) {
        int score = triviaScoreService.getPlayerScore(event.getAuthor().getId());
        return new MessageBuilder().appendFormat("%s, your score is %s", event.getMember().getEffectiveName(), Integer.toString(score)).build();
    }

    private Message displayTriviaCategories() {
        Map<Integer, String> triviaCategoryMap = triviaManager.getTriviaCategories();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trivia Categories");
        List<Integer> keyList = new ArrayList<>(triviaCategoryMap.keySet());
        Collections.sort(keyList);

        for (val categoryKey : keyList) {
            embedBuilder.appendDescription(String.format("ID: %s %s%n", categoryKey, triviaCategoryMap.get(categoryKey)));
        }
        embedBuilder.setFooter("Use the ID to pick a specific category when starting trivia", null);
        return new MessageBuilder(embedBuilder).build();
    }


    private void stopTrivia(MessageReceivedEvent event,
            CommandResult commandResult) {
        if (!triviaManager.isTriviaActiveInChannel(event.getChannel().getId())) {
            commandResult.addUnsuccessfulReaction();
            return;
        }
        triviaManager.stopTrivia(event.getChannel().getId());
        commandResult.addChannelAction("Trivia has ended.");
        commandResult.addSuccessfulReaction();
    }

    private void startTrivia(MessageReceivedEvent event,
            CommandResult commandResult) {
        val channel = event.getChannel();
        if (triviaManager.isTriviaActiveInChannel(channel.getId())) {
            commandResult.addUnsuccessfulReaction();
            return;
        }
        val message = event.getMessage().getContentStripped();
        int categoryId = 0;
        if (getCommandLength(message) == 4 && NumberUtils.isParsable(message.split("\\s+")[3])) {
            categoryId = Integer.parseInt(message.split("\\s+")[3]);
        }
        triviaManager.startTrivia(channel.getId(), categoryId, event.getGuild().getId(), event.getJDA());
    }
}

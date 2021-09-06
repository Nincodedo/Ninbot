package dev.nincodedo.ninbot.components.trivia;

import dev.nincodedo.ninbot.common.command.AbstractCommand;
import dev.nincodedo.ninbot.common.message.MessageReceivedEventMessageExecutor;
import dev.nincodedo.ninbot.components.trivia.game.TriviaManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class TriviaCommand extends AbstractCommand {

    private TriviaManager triviaManager;
    private TriviaScoreService triviaScoreService;

    public TriviaCommand(TriviaManager triviaManager, TriviaScoreService triviaScoreService) {
        name = "trivia";
        length = 3;
        checkExactLength = false;
        this.triviaManager = triviaManager;
        this.triviaScoreService = triviaScoreService;
    }

    //TODO implement SlashCommand
    @Override
    protected MessageReceivedEventMessageExecutor executeCommand(PrivateMessageReceivedEvent event) {
        MessageReceivedEventMessageExecutor messageAction = new MessageReceivedEventMessageExecutor(event);

        return messageAction;
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
            var member = event.getGuild().getMemberById(triviaScore.getUserId());
            if (member != null) {
                embedBuilder.addField(member.getEffectiveName(), Integer.toString(triviaScore.getScore()), false);
            }
        }
        return Optional.of(new MessageBuilder(embedBuilder).build());
    }

    private Message getPlayerScore(MessageReceivedEvent event) {
        int score = triviaScoreService.getPlayerScore(event.getAuthor().getId());
        return new MessageBuilder().appendFormat("%s, your score is %s", event.getMember()
                .getEffectiveName(), Integer.toString(score)).build();
    }

    private Message displayTriviaCategories() {
        Map<Integer, String> triviaCategoryMap = triviaManager.getTriviaCategories();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trivia Categories");
        List<Integer> keyList = new ArrayList<>(triviaCategoryMap.keySet());
        Collections.sort(keyList);

        for (var categoryKey : keyList) {
            embedBuilder.appendDescription(String.format("ID: %s %s%n", categoryKey,
                    triviaCategoryMap.get(categoryKey)));
        }
        embedBuilder.setFooter("Use the ID to pick a specific category when starting trivia", null);
        return new MessageBuilder(embedBuilder).build();
    }


    private void stopTrivia(MessageReceivedEvent event,
            MessageReceivedEventMessageExecutor messageAction) {
        if (!triviaManager.isTriviaActiveInChannel(event.getChannel().getId())) {
            messageAction.addUnsuccessfulReaction();
            return;
        }
        triviaManager.stopTrivia(event.getChannel().getId());
        messageAction.addChannelAction("Trivia has ended.");
        messageAction.addSuccessfulReaction();
    }

    private void startTrivia(MessageReceivedEvent event,
            MessageReceivedEventMessageExecutor messageAction) {
        var channel = event.getChannel();
        if (triviaManager.isTriviaActiveInChannel(channel.getId())) {
            messageAction.addUnsuccessfulReaction();
            return;
        }
        var message = event.getMessage().getContentStripped();
        int categoryId = 0;
        if (getCommandLength(message) == 4 && NumberUtils.isParsable(message.split("\\s+")[3])) {
            categoryId = Integer.parseInt(message.split("\\s+")[3]);
        }
        triviaManager.startTrivia(channel.getId(), categoryId, event.getGuild().getId(), event.getJDA());
    }
}

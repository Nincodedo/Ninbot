package dev.nincodedo.ninbot.components.trivia;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.trivia.game.TriviaManager;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
        this.triviaManager = triviaManager;
        this.triviaScoreService = triviaScoreService;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "start" -> startTrivia(event, messageAction);
            case "stop" -> stopTrivia(event, messageAction);
            case "categories" -> messageAction.addChannelAction(displayTriviaCategories());
            case "score" -> messageAction.addChannelAction(getPlayerScore(event));
            case "leaderboard" -> displayLeaderboard(event).ifPresent(messageAction::addChannelAction);
            default -> messageAction.addUnsuccessfulReaction();
        }
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
            val member = event.getGuild().getMemberById(triviaScore.getUserId());
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

        for (val categoryKey : keyList) {
            embedBuilder.appendDescription(String.format("ID: %s %s%n", categoryKey,
                    triviaCategoryMap.get(categoryKey)));
        }
        embedBuilder.setFooter("Use the ID to pick a specific category when starting trivia", null);
        return new MessageBuilder(embedBuilder).build();
    }


    private void stopTrivia(MessageReceivedEvent event,
            MessageAction messageAction) {
        if (!triviaManager.isTriviaActiveInChannel(event.getChannel().getId())) {
            messageAction.addUnsuccessfulReaction();
            return;
        }
        triviaManager.stopTrivia(event.getChannel().getId());
        messageAction.addChannelAction("Trivia has ended.");
        messageAction.addSuccessfulReaction();
    }

    private void startTrivia(MessageReceivedEvent event,
            MessageAction messageAction) {
        val channel = event.getChannel();
        if (triviaManager.isTriviaActiveInChannel(channel.getId())) {
            messageAction.addUnsuccessfulReaction();
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

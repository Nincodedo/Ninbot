package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class TriviaCommand extends AbstractCommand {

    private TriviaManager triviaManager;

    public TriviaCommand(ConfigService configService, TriviaManager triviaManager) {
        name = "trivia";
        description = "Starts/Stops trivia";
        length = 3;
        checkExactLength = false;
        helpText = "?";
        this.triviaManager = triviaManager;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "start":
                startTrivia(event);
                break;
            case "stop":
                stopTrivia(event);
                break;
            case "categories":
                displayTriviaCategories(event);
                break;
            case "score":
                getPlayerScore(event);
                break;
            case "bad":
                reportBadQuestion(event);
                break;
            default:
                messageUtils.reactUnsuccessfulResponse(event.getMessage());
                break;
        }
    }

    private void getPlayerScore(MessageReceivedEvent event) {
        int score = triviaManager.getPlayerScore(event.getAuthor().getId());
        messageUtils.sendMessage(event.getChannel(), "%s, your score is %s", event.getMember().getEffectiveName(), Integer.toString(score));
    }

    private void reportBadQuestion(MessageReceivedEvent event) {
        //TODO
    }

    private void displayTriviaCategories(MessageReceivedEvent event) {
        Map<Integer, String> triviaCategoryMap = triviaManager.getTriviaCategories();
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trivia Categories");
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> keyList = new ArrayList<>(triviaCategoryMap.keySet());
        Collections.sort(keyList);

        for (val categoryKey : keyList) {
            stringBuilder.append("ID: ").append(categoryKey).append(" ").append(triviaCategoryMap.get(categoryKey)).append("\n");
        }
        embedBuilder.setDescription(stringBuilder.toString());
        embedBuilder.setFooter("Use the ID to pick a specific category when starting trivia", null);
        messageBuilder.setEmbed(embedBuilder.build());
        messageUtils.sendMessage(event.getChannel(), messageBuilder.build());
    }


    private void stopTrivia(MessageReceivedEvent event) {
        if (!triviaManager.isTriviaActiveInChannel(event.getChannel().getId())) {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
            return;
        }
        triviaManager.stopTrivia(event.getChannel().getId());
        messageUtils.sendMessage(event.getChannel(), "Trivia has ended.");
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }

    private void startTrivia(MessageReceivedEvent event) {
        val channel = event.getChannel();
        if (triviaManager.isTriviaActiveInChannel(channel.getId())) {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
            return;
        }
        val message = event.getMessage().getContentStripped();
        int categoryId = 0;
        if (getCommandLength(message) == 4 && NumberUtils.isParsable(message.split(" ")[3])) {
            categoryId = Integer.parseInt(message.split(" ")[3]);
        }
        triviaManager.startTrivia(channel.getId(), categoryId, event.getGuild().getId(), event.getJDA());
    }
}

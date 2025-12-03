package dev.nincodedo.ninbot.components.haiku;

import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.command.message.MessageContextCommand;
import dev.nincodedo.nincord.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Component
public class HaikuAnalyzeCommand implements MessageContextCommand {

    private HaikuMessageParser haikuMessageParser;

    public HaikuAnalyzeCommand(HaikuMessageParser haikuMessageParser) {
        this.haikuMessageParser = haikuMessageParser;
    }

    @Override
    public MessageExecutor execute(@NotNull MessageContextInteractionEvent event,
            @NotNull MessageContextInteractionEventMessageExecutor messageExecutor) {

        EmbedBuilder embedBuilder = buildHaikuAnalysisMessage(event);

        MessageCreateBuilder createBuilder = new MessageCreateBuilder();
        createBuilder.addEmbeds(embedBuilder.build());
        createBuilder.addComponents(ActionRow.of(Button.primary("haiku-share", "Share to channel")));

        messageExecutor.addEphemeralMessage(createBuilder.build());
        return messageExecutor;
    }

    private @NotNull EmbedBuilder buildHaikuAnalysisMessage(@NotNull MessageContextInteractionEvent event) {
        var message = getContentStrippedMessage(event);
        var rawMessage = getRawMessage(event);
        var initialResults = new HaikuInitialAnalysisResult(!message.isEmpty(),
                haikuMessageParser.isMessageOnlyCharacters(message),
                haikuMessageParser.getSyllableCount(message) == 17, haikuMessageParser.getSyllableCount(message));
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Message Haikuability Analysis");
        embedBuilder.setUrl(event.getTarget().getJumpUrl());
        embedBuilder.addField("Has text", Emojis.getCheckOrXResponse(initialResults.messageHasCharacters()), true);
        if (initialResults.messageHasCharacters()) {
            embedBuilder.addField("Only characters",
                    Emojis.getCheckOrXResponse(initialResults.messageOnlyCharacters()), true);
        }
        if (initialResults.messageOnlyCharacters()) {
            embedBuilder.addField("17 syllables",
                    Emojis.getCheckOrXResponse(initialResults.messageIsCorrectSyllables()), true);
        }
        HaikuLineAnalysisResult lineAnalysisResult;
        if (initialResults.messageOnlyCharactersAndIsCorrectSyllables()) {
            lineAnalysisResult = getHaikuLineAnalysisResult(message, embedBuilder, rawMessage);
        } else if (initialResults.messageOnlyCharacters()) {
            var splitMessage = message.split("\\s+");
            StringBuilder messageAnalysis = new StringBuilder();
            for (String word : splitMessage) {
                var wordSyllable = haikuMessageParser.getSyllableCount(word);
                messageAnalysis.append(word).append(" (").append(wordSyllable).append(") ");
            }
            embedBuilder.addField("Message Analysis", MessageUtils.addSpoilerText(messageAnalysis.toString(),
                    rawMessage), false);
            lineAnalysisResult = null;
        } else {
            lineAnalysisResult = null;
        }

        String overallResponse = overallResponseMessage(initialResults, lineAnalysisResult);

        embedBuilder.addField("Overall", overallResponse, false);
        return embedBuilder;
    }

    private @NotNull HaikuLineAnalysisResult getHaikuLineAnalysisResult(String message, EmbedBuilder embedBuilder,
            String rawMessage) {
        HaikuLineAnalysisResult lineAnalysisResult;
        List<Integer> lineTotals = new ArrayList<>();
        var splitMessage = message.split("\\s+");
        StringBuilder lines = new StringBuilder();
        int syllableTotal = 0;
        int lineSyllableTotal = 0;
        int nextSyllableCount = 5;
        for (int i = 0; i < splitMessage.length; i++) {
            var word = splitMessage[i];
            var wordSyllable = haikuMessageParser.getSyllableCount(word);
            syllableTotal += wordSyllable;
            lineSyllableTotal += wordSyllable;
            lines.append(word).append(" (").append(wordSyllable).append(") ");
            if (lineSyllableTotal >= nextSyllableCount) {
                lines.append(String.format(" = %s actual, %s expected", lineSyllableTotal, nextSyllableCount));
                lines.append("\n");
                nextSyllableCount = nextSyllableCount == 7 ? 5 : 7;
                lineTotals.add(lineSyllableTotal);
                lineSyllableTotal = 0;
            }
            if ((syllableTotal >= 17 || lineTotals.size() == 3 || i == splitMessage.length - 1)
                    && lineSyllableTotal > 0) {
                lines.append(String.format(" = %s actual, %s expected", lineSyllableTotal, nextSyllableCount));
                lineTotals.add(lineSyllableTotal);
                break;
            }
        }
        lineAnalysisResult = new HaikuLineAnalysisResult(lineTotals);
        embedBuilder.addField("Line Analysis", MessageUtils.addSpoilerText(lines.toString(), rawMessage), false);
        return lineAnalysisResult;
    }

    private @NotNull String overallResponseMessage(HaikuInitialAnalysisResult initialAnalysisResult,
            @Nullable HaikuLineAnalysisResult lineAnalysisResult) {
        String overallResponse;
        if (initialAnalysisResult.allResultsPass() && lineAnalysisResult != null
                && lineAnalysisResult.allResultsPass()) {
            overallResponse = "Haikuable";
        } else {
            overallResponse = unhaikuableResponseMessage(initialAnalysisResult, lineAnalysisResult);
        }
        return overallResponse;
    }

    private @NotNull String unhaikuableResponseMessage(HaikuInitialAnalysisResult initialAnalysisResult,
            HaikuLineAnalysisResult lineAnalysisResult) {
        String overallResponse = "Not Haikuable";
        String additionalReason = ". Too %s %s.";
        if (!initialAnalysisResult.messageHasCharacters()) {
            overallResponse += ". Message has no text.";
        } else if (!initialAnalysisResult.messageOnlyCharacters()) {
            overallResponse += ". Message has unsyllable characters.";
        } else if (!initialAnalysisResult.calculatedSyllableTotalPass()) {
            overallResponse += String.format(additionalReason, getFewOrMany(
                    initialAnalysisResult.calculatedSyllableTotal()
                            < 17), String.format("syllables: %s", initialAnalysisResult.calculatedSyllableTotal()));
        } else if (!lineAnalysisResult.correctNumberOfLines()) {
            overallResponse += String.format(additionalReason, getFewOrMany(
                    lineAnalysisResult.lineTotals().size() < 3), "lines");
        } else if (!lineAnalysisResult.line1SyllablesCorrect()) {
            overallResponse += String.format(additionalReason, getFewOrMany(
                    lineAnalysisResult.syllablesByLine(0) < 5), " syllables in line 1");
        } else if (!lineAnalysisResult.line2SyllablesCorrect()) {
            overallResponse += String.format(additionalReason, getFewOrMany(
                    lineAnalysisResult.syllablesByLine(1) < 7), "syllables in line 2");
        } else if (!lineAnalysisResult.line3SyllablesCorrect()) {
            overallResponse += String.format(additionalReason, getFewOrMany(
                    lineAnalysisResult.syllablesByLine(2) < 5), "syllables in line 3");
        }
        return overallResponse;
    }

    private @NotNull String getContentStrippedMessage(@NotNull MessageContextInteractionEvent event) {
        var message = event.getTarget();
        if (message.getAuthor().equals(event.getJDA().getSelfUser())) {
            var embeds = event.getTarget().getEmbeds();
            return embeds.getFirst().getDescription() == null ? "" : MarkdownSanitizer.sanitize(embeds.getFirst()
                    .getDescription());
        } else {
            return event.getTarget().getContentStripped();
        }
    }

    private @NotNull String getRawMessage(@NotNull MessageContextInteractionEvent event) {
        var message = event.getTarget();
        if (message.getAuthor().equals(event.getJDA().getSelfUser())) {
            var embeds = event.getTarget().getEmbeds();
            return embeds.getFirst().getDescription() == null ? "" : embeds.getFirst().getDescription();
        } else {
            return event.getTarget().getContentRaw();
        }
    }

    private @NotNull String getFewOrMany(boolean isLessThan) {
        return isLessThan ? "few" : "many";
    }

    @Override
    public String getName() {
        return HaikuCommandName.HAIKU.get();
    }
}

package dev.nincodedo.ninbot.components.haiku;

import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.command.message.MessageContextCommand;
import dev.nincodedo.nincord.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HaikuAnalyzeCommand implements MessageContextCommand {

    private HaikuMessageParser haikuMessageParser;

    public HaikuAnalyzeCommand(HaikuMessageParser haikuMessageParser) {
        this.haikuMessageParser = haikuMessageParser;
    }

    @Override
    public MessageExecutor execute(@NotNull MessageContextInteractionEvent event, @NotNull MessageContextInteractionEventMessageExecutor messageExecutor) {
        var message = getContentStrippedMessage(event);
        var rawMessage = getRawMessage(event);
        boolean messageHasCharacters = !message.isEmpty();
        boolean messageOnlyCharacters = haikuMessageParser.isMessageOnlyCharacters(message);
        boolean messageIsCorrectSyllables = haikuMessageParser.getSyllableCount(message) == 17;
        int calculatedSyllableTotal = haikuMessageParser.getSyllableCount(message);

        boolean correctNumberOfLines = false;
        boolean line1SyllablesCorrect = false;
        boolean line2SyllablesCorrect = false;
        boolean line3SyllablesCorrect = false;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Message Haikuability Analysis");
        embedBuilder.setUrl(event.getInteraction().getTarget().getJumpUrl());
        embedBuilder.addField("Has text", Emojis.getCheckOrXResponse(messageHasCharacters), true);
        if (messageHasCharacters) {
            embedBuilder.addField("Only characters", Emojis.getCheckOrXResponse(messageOnlyCharacters), true);
        }
        if (messageOnlyCharacters) {
            embedBuilder.addField("17 syllables", Emojis.getCheckOrXResponse(messageIsCorrectSyllables), true);
        }
        List<Integer> lineTotals = new ArrayList<>();
        if (messageOnlyCharacters && messageIsCorrectSyllables) {
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
                    if (nextSyllableCount == 7) {
                        nextSyllableCount = 5;
                    } else {
                        nextSyllableCount = 7;
                    }
                    lineTotals.add(lineSyllableTotal);
                    lineSyllableTotal = 0;
                }
                if ((syllableTotal >= 17 || lineTotals.size() == 3 || i == splitMessage.length - 1) && lineSyllableTotal > 0) {
                    lines.append(String.format(" = %s actual, %s expected", lineSyllableTotal, nextSyllableCount));
                    lineTotals.add(lineSyllableTotal);
                    break;
                }
            }
            correctNumberOfLines = lineTotals.size() == 3;
            line1SyllablesCorrect = !lineTotals.isEmpty() && lineTotals.get(0) == 5;
            line2SyllablesCorrect = lineTotals.size() >= 2 && lineTotals.get(1) == 7;
            line3SyllablesCorrect = lineTotals.size() == 3 && lineTotals.get(2) == 5;
            embedBuilder.addField("Line Analysis", MessageUtils.addSpoilerText(lines.toString(), rawMessage), false);
        } else if (messageOnlyCharacters) {
            var splitMessage = message.split("\\s+");
            StringBuilder messageAnalysis = new StringBuilder();
            for (String word : splitMessage) {
                var wordSyllable = haikuMessageParser.getSyllableCount(word);
                messageAnalysis.append(word).append(" (").append(wordSyllable).append(") ");
            }
            embedBuilder.addField("Message Analysis", MessageUtils.addSpoilerText(messageAnalysis.toString(), rawMessage), false);
        }

        String overallResponse = overallResponseMessage(messageOnlyCharacters, messageIsCorrectSyllables, correctNumberOfLines, line1SyllablesCorrect, line2SyllablesCorrect, line3SyllablesCorrect, messageHasCharacters, calculatedSyllableTotal, lineTotals);

        embedBuilder.addField("Overall", overallResponse, false);

        MessageCreateBuilder createBuilder = new MessageCreateBuilder();
        createBuilder.addEmbeds(embedBuilder.build());
        createBuilder.addActionRow(Button.primary("haiku-share", "Share to channel"));

        messageExecutor.addEphemeralMessage(createBuilder.build());
        return messageExecutor;
    }

    private @NotNull String overallResponseMessage(boolean messageOnlyCharacters, boolean messageIsCorrectSyllables, boolean correctNumberOfLines, boolean line1SyllablesCorrect, boolean line2SyllablesCorrect, boolean line3SyllablesCorrect, boolean messageHasCharacters, int calculatedSyllableTotal, List<Integer> lineTotals) {
        String overallResponse;
        if (messageOnlyCharacters && messageIsCorrectSyllables && correctNumberOfLines && line1SyllablesCorrect && line2SyllablesCorrect && line3SyllablesCorrect) {
            overallResponse = "Haikuable";
        } else {
            overallResponse = "Not Haikuable";
            String additionalReason = ". Too %s %s.";
            if (!messageHasCharacters) {
                overallResponse += ". Message has no text.";
            } else if (!messageOnlyCharacters) {
                overallResponse += ". Message has unsyllable characters.";
            } else if (calculatedSyllableTotal != 17) {
                overallResponse += String.format(additionalReason, getFewOrMany(calculatedSyllableTotal < 17), String.format("syllables: %s", calculatedSyllableTotal));
            } else if (lineTotals.size() != 3) {
                overallResponse += String.format(additionalReason, getFewOrMany(lineTotals.size() < 3), "lines");
            } else if (lineTotals.get(0) != 5) {
                overallResponse += String.format(additionalReason, getFewOrMany(lineTotals.getFirst() < 5), " syllables in line 1");
            } else if (lineTotals.get(1) != 7) {
                overallResponse += String.format(additionalReason, getFewOrMany(lineTotals.get(1) < 7), "syllables in line 2");
            } else if (lineTotals.get(2) != 5) {
                overallResponse += String.format(additionalReason, getFewOrMany(lineTotals.get(2) < 5), "syllables in line 3");
            } else {
                overallResponse += ". Heck I dunno how you got here.";
            }
        }
        return overallResponse;
    }

    private @NotNull String getContentStrippedMessage(@NotNull MessageContextInteractionEvent event) {
        var message = event.getTarget();
        if (message.getAuthor().equals(event.getJDA().getSelfUser())) {
            var embeds = event.getTarget().getEmbeds();
            return embeds.getFirst().getDescription() == null ? "" : MarkdownSanitizer.sanitize(embeds.getFirst().getDescription());
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

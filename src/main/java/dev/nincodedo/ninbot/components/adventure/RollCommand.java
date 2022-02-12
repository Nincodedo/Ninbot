package dev.nincodedo.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RollCommand implements SlashCommand {

    private final DiceParser parser;
    private final DiceInterpreter<RollHistory> roller;

    public RollCommand() {
        parser = new DefaultDiceParser();
        roller = new DiceRoller();
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        var notationOption = slashCommandEvent.getOption(RollCommandName.Option.NOTATION.get());
        var notation = notationOption == null ? "1d20" : notationOption.getAsString();
        messageExecutor.addMessageResponse(rollDice(notation, slashCommandEvent.getMember().getEffectiveName()));
        return messageExecutor;
    }

    private Message rollDice(String diceArgs, String memberEffectiveName) {
        var parsed = parser.parse(diceArgs, roller);
        var diceCommand = diceArgs.split("d");
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.appendFormat(resourceBundle().getString("command.roll.result"), memberEffectiveName,
                diceCommand[0], diceCommand[1], String
                        .valueOf(parsed.getTotalRoll()), parsed.toString());
        return messageBuilder.build();
    }

    @Override
    public String getName() {
        return RollCommandName.ROLL.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.STRING, RollCommandName.Option.NOTATION.get(), "Simple dice notation"
                + ". Defaults to 1d20."));
    }
}

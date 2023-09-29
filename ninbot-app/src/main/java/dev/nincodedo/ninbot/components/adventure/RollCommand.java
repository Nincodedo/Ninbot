package dev.nincodedo.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
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
    public MessageExecutor execute(
            @NotNull SlashCommandInteractionEvent event, @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        var notation = event.getOption(RollCommandName.Option.NOTATION.get(), "1d20",
                OptionMapping::getAsString);
        messageExecutor.addMessageResponse(rollDice(notation, event.getMember().getEffectiveName()));
        return messageExecutor;
    }

    private MessageCreateData rollDice(String diceArgs, String memberEffectiveName) {
        var parsed = parser.parse(diceArgs, roller);
        var diceCommand = diceArgs.split("d");
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent(resourceBundle().getString("command.roll.result")
                .formatted(memberEffectiveName, diceCommand[0], diceCommand[1], parsed.getTotalRoll(), parsed));
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

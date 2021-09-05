package dev.nincodedo.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RollCommand implements SlashCommand {

    private final DiceParser parser;

    private final DiceInterpreter<RollHistory> roller;

    public RollCommand() {
        parser = new DefaultDiceParser();
        roller = new DiceRoller();
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
        return "roll";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.STRING, "notation", "Simple dice notation"));
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        if (slashCommandEvent.getOption("notation") != null) {
            String notation = slashCommandEvent.getOption("notation").getAsString();
            slashCommandEvent.reply(rollDice(notation, slashCommandEvent.getMember().getEffectiveName())).queue();
        } else if (slashCommandEvent.getOption("notation") == null) {
            slashCommandEvent.reply(rollDice("1d20", slashCommandEvent.getMember().getEffectiveName())).queue();
        } else {
            slashCommandEvent.reply("wot").queue();
        }
    }
}

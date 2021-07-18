package dev.nincodedo.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.command.SlashCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RollCommand extends AbstractCommand implements SlashCommand {

    private final DiceParser parser;

    private final DiceInterpreter<RollHistory> roller;

    public RollCommand() {
        name = "roll";
        checkExactLength = false;
        length = 2;
        parser = new DefaultDiceParser();
        roller = new DiceRoller();
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val content = event.getMessage().getContentStripped();
        val commandArgs = content.split("\\s+");
        if (commandArgs.length == 2) {
            messageAction.addChannelAction(rollDice("1d20", event.getMember().getEffectiveName()));
        } else if (commandArgs.length == 3 && commandArgs[2].contains("d")) {
            messageAction.addChannelAction(rollDice(commandArgs[2], event.getMember().getEffectiveName()));
        } else {
            messageAction.addUnknownReaction();
        }
        return messageAction;
    }

    private Message rollDice(String diceArgs, String memberEffectiveName) {
        val parsed = parser.parse(diceArgs, roller);
        val diceCommand = diceArgs.split("d");
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.appendFormat(resourceBundle.getString("command.roll.result"), memberEffectiveName,
                diceCommand[0], diceCommand[1], String
                .valueOf(parsed.getTotalRoll()), parsed.toString());
        return messageBuilder.build();
    }

    @Override
    public String getDescription() {
        return ResourceBundle.getBundle("lang", Locale.ENGLISH).getString("command.roll.description.text");
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

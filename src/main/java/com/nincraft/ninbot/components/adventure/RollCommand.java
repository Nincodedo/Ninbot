package com.nincraft.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import lombok.val;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class RollCommand extends AbstractCommand {

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
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val content = event.getMessage().getContentStripped();
        val commandArgs = content.split("\\s+");
        if (commandArgs.length == 2) {
            commandResult.addChannelAction(rollDice("1d20", event));
        } else if (commandArgs.length == 3 && commandArgs[2].contains("d")) {
            commandResult.addChannelAction(rollDice(commandArgs[2], event));
        } else {
            commandResult.addUnknownReaction();
        }
        return commandResult;
    }

    private Message rollDice(String diceArgs, MessageReceivedEvent event) {
        val parsed = parser.parse(diceArgs, roller);
        val diceCommand = diceArgs.split("d");
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.appendFormat(resourceBundle.getString("command.roll.result"), event.getAuthor()
                .getName(), diceCommand[0], diceCommand[1], String.valueOf(parsed.getTotalRoll()));
        return messageBuilder.build();
    }
}

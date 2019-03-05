package com.nincraft.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandAction;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class RollCommand extends AbstractCommand {

    private final DiceParser parser;
    
    private final DiceInterpreter<RollHistory> roller;

    public RollCommand() {
        name = "roll";
        checkExactLength = false;
        length = 2;
        description = "Rolls dice which is nice";
        parser = new DefaultDiceParser();
        roller = new DiceRoller();
    }

    @Override
    public Optional<CommandResult> executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val content = event.getMessage().getContentStripped();
        val commandArgs = content.split("\\s+");
        if (commandArgs.length == 2) {
            commandResult.addAction(CommandAction.CHANNEL_MESSAGE, rollDice("1d20", event));
        } else if (commandArgs.length == 3 && commandArgs[2].contains("d")) {
            commandResult.addAction(CommandAction.CHANNEL_MESSAGE, rollDice(commandArgs[2], event));
        } else {
            commandResult.addAction(Emojis.QUESTION_MARK);
        }
        return Optional.of(commandResult);
    }

    private void rollDice(String diceArgs, MessageReceivedEvent event) {
        val parsed = parser.parse(diceArgs, roller);
        val diceCommand = diceArgs.split("d");
        messageUtils.sendMessage(event.getChannel(), "%s rolled %s %s sided dice, result %s", event.getAuthor().getName(), diceCommand[0], diceCommand[1], String.valueOf(parsed.getTotalRoll()));
    }
}

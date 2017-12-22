package com.nincraft.ninbot.command;

import com.bernardomg.tabletop.dice.parser.DefaultDiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.roller.DefaultRoller;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RollCommand extends AbstractCommand {

    private DefaultDiceNotationExpressionParser parser;

    public RollCommand() {
        name = "roll";
        checkExactLength = false;
        length = 2;
        parser = new DefaultDiceNotationExpressionParser(new DefaultRoller());
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val content = event.getMessage().getContent();
        val commandArgs = content.split(" ");
        if (commandArgs.length == 2) {
            rollDice("1d20", event);
        } else if (commandArgs.length == 3 && commandArgs[2].contains("d")) {
            rollDice(commandArgs[2], event);
        }
    }

    private void rollDice(String diceArgs, MessageReceivedEvent event) {
        val parsed = parser.parse(diceArgs);
        val diceCommand = diceArgs.split("d");
        MessageUtils.sendMessage(event.getChannel(), "%s rolled %s %s sided dice, result %s", event.getAuthor().getName(), diceCommand[0], diceCommand[1], String.valueOf(parsed.getValue()));
    }
}

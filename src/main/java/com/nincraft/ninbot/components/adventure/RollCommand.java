package com.nincraft.ninbot.components.adventure;

import com.bernardomg.tabletop.dice.parser.DefaultDiceNotationExpressionParser;
import com.bernardomg.tabletop.dice.roller.DefaultRoller;
import com.nincraft.ninbot.components.command.AbstractCommand;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class RollCommand extends AbstractCommand {

    private DefaultDiceNotationExpressionParser parser;

    public RollCommand() {
        name = "roll";
        checkExactLength = false;
        length = 2;
        description = "Rolls dice which is nice";
        parser = new DefaultDiceNotationExpressionParser(new DefaultRoller());
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val content = event.getMessage().getContentStripped();
        val commandArgs = content.split(" ");
        if (commandArgs.length == 2) {
            rollDice("1d20", event);
        } else if (commandArgs.length == 3 && commandArgs[2].contains("d")) {
            rollDice(commandArgs[2], event);
        } else {
            messageUtils.reactUnknownResponse(event.getMessage());
        }
    }

    private void rollDice(String diceArgs, MessageReceivedEvent event) {
        val parsed = parser.parse(diceArgs);
        val diceCommand = diceArgs.split("d");
        messageUtils.sendMessage(event.getChannel(), "%s rolled %s %s sided dice, result %s", event.getAuthor().getName(), diceCommand[0], diceCommand[1], String.valueOf(parsed.roll()));
    }
}

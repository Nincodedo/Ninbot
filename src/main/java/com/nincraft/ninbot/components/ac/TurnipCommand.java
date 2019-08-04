package com.nincraft.ninbot.components.ac;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class TurnipCommand extends AbstractCommand {

    public TurnipCommand(TurnipPricesRepository turnipPricesRepository) {
        name = "turnip";
    }


    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);

        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "join":
                commandResult.addCorrectReaction(joinTurnipEvent(event));
                break;
            case "buy":
                commandResult.addCorrectReaction(buyTurnips(event));
                break;
            case "sell":
                commandResult.addCorrectReaction(sellTurnips(event));
                break;
            case "price":
                commandResult.addChannelAction(listTurnipPrices(event));
                break;
            default:
                commandResult = displayHelp(event);
                break;
        }

        return commandResult;
    }

    private boolean joinTurnipEvent(MessageReceivedEvent event) {
        return false;
    }

    private Message listTurnipPrices(MessageReceivedEvent event) {
        return null;
    }

    private boolean sellTurnips(MessageReceivedEvent event) {
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }
        return false;
    }

    private boolean buyTurnips(MessageReceivedEvent event) {
        if (!LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }


        return true;
    }
}

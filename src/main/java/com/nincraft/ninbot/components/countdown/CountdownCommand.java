package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CountdownCommand extends AbstractCommand {

    private CountdownDao countdownDao;
    private CountdownScheduler countdownScheduler;

    public CountdownCommand(CountdownDao countdownDao, CountdownScheduler countdownScheduler) {
        name = "countdown";
        description = "Setup a countdown to an event";
        length = 2;
        checkExactLength = false;
        helpText = "Use \"@Ninbot countdown YYYY-MM-DD CountdownName\" to setup a countdown. It will be announced every day leading up to the event.";
        this.countdownDao = countdownDao;
        this.countdownScheduler = countdownScheduler;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list":
                commandResult.addChannelAction(listCountdowns(event));
                break;
            case "":
                commandResult = displayHelp(event);
                break;
            default:
                commandResult.addCorrectReaction(setupCountdown(event));
                break;
        }
        return commandResult;
    }

    private Message listCountdowns(MessageReceivedEvent event) {
        val list = countdownDao.getAllObjectsByServerId(event.getGuild().getId());
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        if (!list.isEmpty()) {
            messageBuilder.setTitle("Current Countdowns");
            for (val countdown : list) {
                messageBuilder.addField(countdown.getName(), countdown.getEventDate().toString(), false);
            }
        } else {
            messageBuilder.setTitle("No countdowns are currently scheduled, use \"@Ninbot countdown\" to add your own!");
        }
        return messageBuilder.build();
    }

    private boolean setupCountdown(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val splitMessage = message.split("\\s+");
        if (splitMessage.length >= 3) {
            val stringDate = splitMessage[2];
            val countdownName = message.substring(message.indexOf(stringDate) + stringDate.length() + 1);
            Countdown countdown = new Countdown();
            countdown.setChannelId(event.getChannel().getId())
                    .setEventDate(LocalDate.parse(stringDate, DateTimeFormatter.ISO_LOCAL_DATE))
                    .setName(countdownName)
                    .setServerId(event.getGuild().getId());
            countdownDao.saveObject(countdown);
            countdownScheduler.scheduleOne(countdown, event.getJDA());
            return true;
        } else {
            return false;
        }
    }
}
package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    protected Optional<CommandResult> executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list":
                listCountdowns(event);
                break;
            case "":
                displayHelp(event);
                break;
            default:
                setupCountdown(event);
                break;
        }
    }

    private void listCountdowns(MessageReceivedEvent event) {
        val channel = event.getChannel();
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
        messageUtils.sendMessage(channel, messageBuilder.build());
    }

    private void setupCountdown(MessageReceivedEvent event) {
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
            messageUtils.reactSuccessfulResponse(event.getMessage());
        } else {
            messageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }
}
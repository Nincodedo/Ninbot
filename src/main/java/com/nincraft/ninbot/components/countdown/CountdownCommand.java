package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
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
        helpText = "Use \"@Ninbot countdown YYYY-MM-DD CountdownName\" to setup a countdown. It will be announced once a week up until the week before the event. Then it will be announced every day leading up to the event.";
        this.countdownDao = countdownDao;
        this.countdownScheduler = countdownScheduler;
    }


    @Override
    protected void executeCommand(MessageReceivedEvent event) {
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
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!list.isEmpty()) {
            embedBuilder.setTitle("Current Countdowns");
            for (val countdown : list) {
                embedBuilder.addField(countdown.getName(), countdown.getEventDate().toString(), false);
            }
        } else {
            embedBuilder.setTitle("No countdowns are currently scheduled, use \"@Ninbot countdown\" to add your own!");
        }
        messageBuilder.setEmbed(embedBuilder.build());
        MessageUtils.sendMessage(channel, messageBuilder.build());
    }

    private void setupCountdown(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val splitMessage = message.split(" ");
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
            MessageUtils.reactSuccessfulResponse(event.getMessage());
        } else {
            MessageUtils.reactUnsuccessfulResponse(event.getMessage());
        }
    }
}
package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class CountdownCommand extends AbstractCommand {

    private CountdownDao countdownDao;

    public CountdownCommand(CountdownDao countdownDao) {
        name = "countdown";
        description = "Setup a countdown to an event";
        length = 2;
        checkExactLength = false;
        helpText = "wow";
        this.countdownDao = countdownDao;
    }


    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list":
                listCountdowns(event.getChannel());
                break;
            default:
                setupCountdown(event);
                break;
        }
    }

    private void listCountdowns(MessageChannel channel) {
        val list = countdownDao.getAllObjects();
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Current Countdowns");
        for (val countdown : list) {
            embedBuilder.addField(countdown.getName(), countdown.getEventDate().toString(), false);
        }
        messageBuilder.setEmbed(embedBuilder.build());
        MessageUtils.sendMessage(channel, messageBuilder.build());
    }

    private void setupCountdown(MessageReceivedEvent event) {

    }
}

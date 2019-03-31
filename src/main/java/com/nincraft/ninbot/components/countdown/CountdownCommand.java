package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CountdownCommand extends AbstractCommand {

    private CountdownDao countdownDao;
    private CountdownScheduler countdownScheduler;
    private ConfigService configService;

    public CountdownCommand(CountdownDao countdownDao, CountdownScheduler countdownScheduler,
            ConfigService configService) {
        name = "countdown";
        description = "Setup a countdown to an event";
        length = 2;
        checkExactLength = false;
        helpText = "Use \"@Ninbot countdown YYYY-MM-DD CountdownName\" to setup a countdown. It will be announced every day leading up to the event.";
        this.countdownDao = countdownDao;
        this.countdownScheduler = countdownScheduler;
        this.configService = configService;
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
                messageBuilder.addField(countdown.getName(),
                        "Start Time: " + countdown.getEventDate().format(DateTimeFormatter.ISO_OFFSET_DATE), false);
            }
            String serverTimezone = getServerTimeZone(event.getGuild().getId());
            messageBuilder.setFooter("All times are shown in " + serverTimezone, null);
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
            ZoneId serverTimezone = ZoneId.of(getServerTimeZone(event.getGuild().getId()));
            Countdown countdown = new Countdown();
            countdown.setChannelId(event.getChannel().getId())
                    .setEventDate(LocalDate.parse(stringDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(serverTimezone))
                    .setName(countdownName)
                    .setServerId(event.getGuild().getId());
            countdownDao.saveObject(countdown);
            countdownScheduler.scheduleOne(countdown, event.getJDA());
            return true;
        } else {
            return false;
        }
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }
}
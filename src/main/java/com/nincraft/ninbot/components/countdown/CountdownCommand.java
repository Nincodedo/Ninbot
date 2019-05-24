package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CountdownCommand extends AbstractCommand {

    private CountdownRepository countdownRepository;
    private CountdownScheduler countdownScheduler;
    private ConfigService configService;

    public CountdownCommand(CountdownRepository countdownRepository, CountdownScheduler countdownScheduler,
            ConfigService configService) {
        name = "countdown";
        length = 2;
        checkExactLength = false;
        this.countdownRepository = countdownRepository;
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
        val list = countdownRepository.findByServerId(event.getGuild().getId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!list.isEmpty()) {
            embedBuilder.setTitle(resourceBundle.getString("command.countdown.list.title"));
            for (val countdown : list) {
                embedBuilder.addField(countdown.getName(),
                        resourceBundle.getString("command.countdown.list.starttime")
                                + countdown.getEventDate().format(DateTimeFormatter.ISO_OFFSET_DATE), false);
            }
            String serverTimezone = getServerTimeZone(event.getGuild().getId());
            embedBuilder.setFooter(resourceBundle.getString("command.countdown.list.footer") + serverTimezone, null);
        } else {
            embedBuilder.setTitle(resourceBundle.getString("command.countdown.list.nocountdownsfound"));
        }
        return new MessageBuilder(embedBuilder).build();
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
            countdownRepository.save(countdown);
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

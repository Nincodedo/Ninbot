package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
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

    public CountdownCommand(CountdownRepository countdownRepository, CountdownScheduler countdownScheduler) {
        name = "countdown";
        length = 2;
        checkExactLength = false;
        this.countdownRepository = countdownRepository;
        this.countdownScheduler = countdownScheduler;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "list" -> messageAction.addChannelAction(listCountdowns(event));
            case "" -> messageAction = displayHelp(event);
            default -> messageAction.addCorrectReaction(setupCountdown(event));
        }
        return messageAction;
    }

    private Message listCountdowns(MessageReceivedEvent event) {
        val list = countdownRepository.findByServerId(event.getGuild().getId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!list.isEmpty()) {
            embedBuilder.setTitle(resourceBundle.getString("command.countdown.list.title"));
            for (val countdown : list) {
                countdown.setResourceBundle(resourceBundle);
                embedBuilder.addField(countdown.getName(), countdown.getDescription(), false);
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
                    .setEventDate(LocalDate.parse(stringDate, DateTimeFormatter.ISO_LOCAL_DATE)
                            .atStartOfDay(serverTimezone))
                    .setName(countdownName)
                    .setServerId(event.getGuild().getId());
            countdownRepository.save(countdown);
            countdownScheduler.scheduleOne(countdown, event.getJDA().getShardManager());
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

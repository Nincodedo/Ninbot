package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Component
public class CountdownCommand implements SlashCommand {

    private CountdownRepository countdownRepository;
    private CountdownScheduler countdownScheduler;
    private ConfigService configService;

    public CountdownCommand(CountdownRepository countdownRepository, CountdownScheduler countdownScheduler,
            ConfigService configService) {
        this.countdownRepository = countdownRepository;
        this.countdownScheduler = countdownScheduler;
        this.configService = configService;
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        var subcommandName = slashCommandEvent.getSubcommandName();
        if (subcommandName == null) {
            return;
        }
        switch (CountdownCommandName.valueOf(slashCommandEvent.getSubcommandName())) {
            case LIST -> slashCommandEvent.reply(listCountdowns(slashCommandEvent)).setEphemeral(true).queue();
            case CREATE -> slashCommandEvent.reply(setupCountdown(slashCommandEvent)).setEphemeral(true).queue();
        }
    }

    private Message listCountdowns(SlashCommandEvent event) {
        var list = countdownRepository.findByServerId(event.getGuild().getId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!list.isEmpty()) {
            embedBuilder.setTitle(resourceBundle().getString("command.countdown.list.title"));
            for (var countdown : list) {
                countdown.setResourceBundle(resourceBundle());
                embedBuilder.addField(countdown.getName(), countdown.getDescription(), false);
            }
            String serverTimezone = getServerTimeZone(event.getGuild().getId());
            embedBuilder.setFooter(resourceBundle().getString("command.countdown.list.footer") + serverTimezone, null);
        } else {
            embedBuilder.setTitle(resourceBundle().getString("command.countdown.list.nocountdownsfound"));
        }
        return new MessageBuilder(embedBuilder).build();
    }

    private Message setupCountdown(SlashCommandEvent slashCommandEvent) {
        var stringDate = getCountdownDate(slashCommandEvent);
        var countdownName = slashCommandEvent.getOption("name").getAsString();
        ZoneId serverTimezone = ZoneId.of(getServerTimeZone(slashCommandEvent.getGuild().getId()));
        Countdown countdown = new Countdown();
        countdown.setChannelId(slashCommandEvent.getChannel().getId())
                .setEventDate(LocalDate.parse(stringDate, ISO_LOCAL_DATE)
                        .atStartOfDay(serverTimezone))
                .setName(countdownName)
                .setServerId(slashCommandEvent.getGuild().getId());
        countdownRepository.save(countdown);
        countdownScheduler.scheduleOne(countdown, slashCommandEvent.getJDA().getShardManager());
        return new MessageBuilder().append(Emojis.CHECK_MARK).build();
    }

    private String getCountdownDate(SlashCommandEvent slashCommandEvent) {
        String countdownDate;
        var year = slashCommandEvent.getOption("year");
        //year is not supplied so we'll figure it out
        if (year == null) {
            var possibleCountdownDate = String.format("%s-%s-%s", LocalDate.now()
                    .getYear(), slashCommandEvent.getOption("month")
                    .getAsString(), slashCommandEvent.getOption("day").getAsString());
            LocalDate thisYearPossibleCountdownDate = LocalDate.parse(possibleCountdownDate,
                    DateTimeFormatter.ISO_LOCAL_DATE);
            if (thisYearPossibleCountdownDate.isAfter(LocalDate.now())) {
                countdownDate = possibleCountdownDate;
            } else {
                countdownDate = String.format("%s-%s-%s",
                        LocalDate.now().getYear() + 1, slashCommandEvent.getOption("month")
                                .getAsString(), slashCommandEvent.getOption("day").getAsString());
            }
        }
        //year is supplied to we'll just use it
        else {
            countdownDate = String.format("%s-%s-%s", year.getAsString(), slashCommandEvent.getOption("month")
                    .getAsString(), slashCommandEvent.getOption("day").getAsString());
        }
        return countdownDate;
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }

    @Override
    public String getName() {
        return "countdown";
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(
                new SubcommandData(CountdownCommandName.CREATE.get(), "Create a new countdown.")
                        .addOption(OptionType.STRING, "name", "The name for this countdown.", true)
                        .addOption(OptionType.STRING, "month", "The numerical month for this countdown.", true)
                        .addOption(OptionType.STRING, "day", "The numerical day for this countdown.", true)
                        .addOption(OptionType.STRING, "year", "The year for this countdown. Defaults to the upcoming "
                                + "date this month and day fall."),
                new SubcommandData(CountdownCommandName.LIST.get(), "List all the current countdowns for this server.")
        );
    }
}

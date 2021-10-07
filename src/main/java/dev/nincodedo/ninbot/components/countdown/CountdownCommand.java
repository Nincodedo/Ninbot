package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Component
public class CountdownCommand implements SlashCommand {

    private final CountdownRepository countdownRepository;
    private final CountdownScheduler countdownScheduler;
    private final ConfigService configService;

    public CountdownCommand(CountdownRepository countdownRepository, CountdownScheduler countdownScheduler,
            ConfigService configService) {
        this.countdownRepository = countdownRepository;
        this.countdownScheduler = countdownScheduler;
        this.configService = configService;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(SlashCommandEvent slashCommandEvent) {
        var subcommandName = slashCommandEvent.getSubcommandName();
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        if (subcommandName == null) {
            return messageExecutor;
        }
        switch (CountdownCommandName.Subcommand.valueOf(slashCommandEvent.getSubcommandName().toUpperCase())) {
            case LIST -> messageExecutor.addEphemeralMessage(listCountdowns(slashCommandEvent));
            case CREATE -> messageExecutor.addEphemeralMessage(setupCountdown(slashCommandEvent));
        }
        return messageExecutor;
    }

    private MessageEmbed listCountdowns(SlashCommandEvent event) {
        var list = countdownRepository.findByServerId(event.getGuild().getId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!list.isEmpty()) {
            embedBuilder.setTitle(resourceBundle().getString("command.countdown.list.title"));
            for (var countdown : list) {
                countdown.setResourceBundle(resourceBundle());
                embedBuilder.addField(countdown.getName(), countdown.getDescription(), false);
            }
        } else {
            embedBuilder.setTitle(resourceBundle().getString("command.countdown.list.nocountdownsfound"));
        }
        return embedBuilder.build();
    }

    private Message setupCountdown(SlashCommandEvent slashCommandEvent) {
        var stringDate = getCountdownDate(slashCommandEvent);
        var countdownName = slashCommandEvent.getOption("name").getAsString();
        ZoneId serverTimezone = ZoneId.of(getServerTimeZone(slashCommandEvent.getGuild().getId()));
        Countdown countdown = new Countdown()
                .setChannelId(slashCommandEvent.getChannel().getId())
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
        var month = String.format("%02d",
                Integer.parseInt(slashCommandEvent.getOption(CountdownCommandName.Option.MONTH.get())
                        .getAsString()));
        var day = String.format("%02d",
                Integer.parseInt(slashCommandEvent.getOption(CountdownCommandName.Option.DAY.get())
                        .getAsString()));
        //year is not supplied so we'll figure it out
        if (year == null) {
            var possibleCountdownDate = getDateFormatted(String.valueOf(LocalDate.now().getYear()), month, day);
            var thisYearPossibleCountdownDate = LocalDate.parse(possibleCountdownDate,
                    DateTimeFormatter.ISO_LOCAL_DATE);
            if (thisYearPossibleCountdownDate.isAfter(LocalDate.now())) {
                countdownDate = possibleCountdownDate;
            } else {
                countdownDate = getDateFormatted(String.valueOf(LocalDate.now().getYear() + 1), month, day);
            }
        }
        //year is supplied to we'll just use it
        else {
            countdownDate = getDateFormatted(year.getAsString(), month, day);
        }
        return countdownDate;
    }

    private String getDateFormatted(String year, String month, String day) {
        return String.format("%s-%s-%s", year, month, day);
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.SERVER_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }

    @Override
    public String getName() {
        return CountdownCommandName.COUNTDOWN.get();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(
                new SubcommandData(CountdownCommandName.Subcommand.CREATE.get(), "Create a new countdown.")
                        .addOption(OptionType.STRING, CountdownCommandName.Option.NAME.get(), "The name for this "
                                + "countdown.", true)
                        .addOption(OptionType.STRING, CountdownCommandName.Option.MONTH.get(), "The numerical month "
                                + "for this countdown.", true)
                        .addOption(OptionType.STRING, CountdownCommandName.Option.DAY.get(), "The numerical day for "
                                + "this countdown.", true)
                        .addOption(OptionType.STRING, CountdownCommandName.Option.YEAR.get(),
                                "The year for this countdown. Defaults to the upcoming "
                                        + "date this month and day fall."),
                new SubcommandData(CountdownCommandName.Subcommand.LIST.get(), "List all the current "
                        + "countdowns for this server.")
        );
    }
}

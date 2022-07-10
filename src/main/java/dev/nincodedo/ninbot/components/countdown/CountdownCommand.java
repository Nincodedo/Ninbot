package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.Subcommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Component
public class CountdownCommand implements SlashCommand, Subcommand<CountdownCommandName.Subcommand> {

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
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var subcommandName = slashCommandEvent.getSubcommandName();
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        if (subcommandName == null) {
            return messageExecutor;
        }
        switch (getSubcommand(subcommandName)) {
            case LIST -> messageExecutor.addMessageEmbed(listCountdowns(slashCommandEvent));
            case CREATE -> messageExecutor.addMessageResponse(setupCountdown(slashCommandEvent));
            case DELETE -> messageExecutor.addEphemeralMessage(deleteCountdown(slashCommandEvent));
        }
        return messageExecutor;
    }

    private Message deleteCountdown(SlashCommandInteractionEvent slashCommandEvent) {
        var countdownName = slashCommandEvent.getOption(CountdownCommandName.Option.NAME.get(),
                OptionMapping::getAsString);
        var userId = slashCommandEvent.getUser().getId();
        var optionalCountdown = countdownRepository.findByCreatedByAndName(userId, countdownName);
        if (optionalCountdown.isPresent()) {
            countdownRepository.delete(optionalCountdown.get());
            return new MessageBuilder().append(resourceBundle().getString("command.countdown.delete.success"))
                    .append(countdownName).build();
        } else {
            return new MessageBuilder().append(resourceBundle().getString("command.countdown.delete.failure"))
                    .append(countdownName).build();
        }
    }

    private MessageEmbed listCountdowns(SlashCommandInteractionEvent event) {
        var countdownList = countdownRepository.findByServerId(event.getGuild().getId());
        countdownList.sort(Comparator.comparing(Countdown::getEventDate));
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!countdownList.isEmpty()) {
            embedBuilder.setTitle(resourceBundle().getString("command.countdown.list.title"));
            for (var countdown : countdownList) {
                countdown.setResourceBundle(resourceBundle());
                String countdownDescription = countdown.getDescription();
                if (countdown.getCreatedBy() != null) {
                    var countdownCreator = event.getGuild().getMemberById(countdown.getCreatedBy());
                    if (countdownCreator != null) {
                        countdownDescription = addCreatorInfo(countdownCreator, countdownDescription);
                    }
                }
                embedBuilder.addField(countdown.getName(), countdownDescription, false);
            }
        } else {
            embedBuilder.setTitle(resourceBundle().getString("command.countdown.list.nocountdownsfound"));
        }
        return embedBuilder.build();
    }

    private String addCreatorInfo(@NotNull Member countdownCreator, String countdownDescription) {
        return countdownDescription + " - "
                + resourceBundle().getString("command.countdown.list.author")
                + countdownCreator.getEffectiveName();
    }

    private Message setupCountdown(SlashCommandInteractionEvent slashCommandEvent) {
        var year = slashCommandEvent.getOption("year", OptionMapping::getAsString);
        var month = String.format("%02d",
                Integer.parseInt(slashCommandEvent.getOption(CountdownCommandName.Option.MONTH.get(),
                        OptionMapping::getAsString)));
        var day = String.format("%02d",
                Integer.parseInt(slashCommandEvent.getOption(CountdownCommandName.Option.DAY.get(),
                        OptionMapping::getAsString)));
        var stringDate = getCountdownDate(year, month, day);
        var countdownName = slashCommandEvent.getOption("name", OptionMapping::getAsString);
        ZoneId serverTimezone = ZoneId.of(getServerTimeZone(slashCommandEvent.getGuild().getId()));
        Countdown countdown = new Countdown()
                .setChannelId(slashCommandEvent.getChannel().getId())
                .setEventDate(LocalDate.parse(stringDate, ISO_LOCAL_DATE)
                        .atStartOfDay(serverTimezone))
                .setName(countdownName)
                .setServerId(slashCommandEvent.getGuild().getId());
        countdown.setCreatedBy(slashCommandEvent.getUser().getId());
        countdownRepository.save(countdown);
        countdownScheduler.scheduleOne(countdown, slashCommandEvent.getJDA().getShardManager());
        countdown.setResourceBundle(resourceBundle(slashCommandEvent.getGuild().getLocale()));
        return new MessageBuilder().append("Created ")
                .append(countdown.getName())
                .append(" ")
                .append("starts ")
                .append(countdown.getEventDateDescription())
                .append(" ")
                .append(Emojis.CHECK_MARK)
                .build();
    }

    private String getCountdownDate(String year, String month, String day) {
        String countdownDate;
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
            countdownDate = getDateFormatted(year, month, day);
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
                        .addOption(OptionType.STRING, CountdownCommandName.Option.YEAR.get(), "The year for this "
                                + "countdown. Defaults to the upcoming date this month and day fall."),
                new SubcommandData(CountdownCommandName.Subcommand.LIST.get(), "List all the current countdowns for "
                        + "this server."),
                new SubcommandData(CountdownCommandName.Subcommand.DELETE.get(), "Delete a countdown you created.")
                        .addOptions(new OptionData(OptionType.STRING, CountdownCommandName.Option.NAME.get(), "The "
                                + "name of the countdown.", true, true))
        );
    }

    @Override
    public Class<CountdownCommandName.Subcommand> enumSubcommandClass() {
        return CountdownCommandName.Subcommand.class;
    }
}

package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.components.countdown.CountdownCommandName.Subcommand;
import dev.nincodedo.nincord.Emojis;
import dev.nincodedo.nincord.command.slash.SlashSubCommand;
import dev.nincodedo.nincord.config.db.ConfigConstants;
import dev.nincodedo.nincord.config.db.ConfigService;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Component
public class CountdownCommand implements SlashSubCommand<Subcommand> {

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
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor, @NotNull Subcommand subcommand) {
        switch (subcommand) {
            case LIST -> messageExecutor.addMessageEmbed(listCountdowns(event));
            case CREATE -> messageExecutor.addMessageResponse(setupCountdown(event));
            case DELETE -> messageExecutor.addEphemeralMessage(deleteCountdown(event));
        }
        return messageExecutor;
    }

    private MessageCreateData deleteCountdown(SlashCommandInteractionEvent slashCommandEvent) {
        var countdownName = slashCommandEvent.getOption(CountdownCommandName.Option.NAME.get(),
                OptionMapping::getAsString);
        if (countdownName == null) {
            return new MessageCreateBuilder().addContent(resourceBundle().getString("command.countdown.delete.failure"))
                    .build();
        }
        var userId = slashCommandEvent.getUser().getId();
        var optionalCountdown = countdownRepository.findByAudit_CreatedByAndName(userId, countdownName);
        if (optionalCountdown.isPresent()) {
            countdownRepository.delete(optionalCountdown.get());
            return new MessageCreateBuilder().addContent(resourceBundle().getString("command.countdown.delete.success"))
                    .addContent(countdownName).build();
        } else {
            return new MessageCreateBuilder().addContent(resourceBundle().getString("command.countdown.delete.failure"))
                    .addContent(countdownName).build();
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
                if (countdown.getAudit().getCreatedBy() != null) {
                    var countdownCreator = event.getGuild().getMemberById(countdown.getAudit().getCreatedBy());
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

    private MessageCreateData setupCountdown(SlashCommandInteractionEvent slashCommandEvent) {
        var year = slashCommandEvent.getOption("year", OptionMapping::getAsString);
        var month = "%02d".formatted(Integer.parseInt(getRequiredOption(slashCommandEvent,
                CountdownCommandName.Option.MONTH.get())));
        var day = "%02d".formatted(Integer.parseInt(getRequiredOption(slashCommandEvent,
                CountdownCommandName.Option.DAY.get())));
        var stringDate = getCountdownDate(year, month, day);
        var countdownName = slashCommandEvent.getOption("name", OptionMapping::getAsString);
        ZoneId serverTimezone = ZoneId.of(getServerTimeZone(slashCommandEvent.getGuild().getId()));
        Countdown countdown = new Countdown()
                .setChannelId(slashCommandEvent.getChannel().getId())
                .setEventDate(LocalDate.parse(stringDate, ISO_LOCAL_DATE)
                        .atStartOfDay(serverTimezone))
                .setName(countdownName)
                .setServerId(slashCommandEvent.getGuild().getId());
        countdown.getAudit().setCreatedBy(slashCommandEvent.getUser().getId());
        countdownRepository.save(countdown);
        countdownScheduler.scheduleOne(countdown, slashCommandEvent.getJDA().getShardManager());
        countdown.setResourceBundle(resourceBundle(slashCommandEvent.getGuild().getLocale()));
        return new MessageCreateBuilder().addContent("Created ")
                .addContent(countdown.getName())
                .addContent(" ")
                .addContent("starts ")
                .addContent(countdown.getEventDateDescription())
                .addContent(" ")
                .addContent(Emojis.CHECK_MARK)
                .build();
    }

    @NotNull
    private String getRequiredOption(SlashCommandInteractionEvent slashCommandEvent, String optionName) {
        return Objects.requireNonNull(slashCommandEvent.getOption(optionName, OptionMapping::getAsString));
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
        return "%s-%s-%s".formatted(year, month, day);
    }

    private String getServerTimeZone(String serverId) {
        return configService.getSingleValueByName(serverId, ConfigConstants.GUILD_TIMEZONE)
                .orElse(ConfigConstants.DEFAULT_TIMEZONE);
    }

    @Override
    public String getName() {
        return CountdownCommandName.COUNTDOWN.get();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(new SubcommandData(Subcommand.CREATE.get(), "Create a new countdown.")
                        .addOption(OptionType.STRING, CountdownCommandName.Option.NAME.get(),
                                "The name for this countdown.", true)
                        .addOption(OptionType.STRING, CountdownCommandName.Option.MONTH.get(),
                                "The numerical month for this countdown.", true)
                        .addOption(OptionType.STRING, CountdownCommandName.Option.DAY.get(),
                                "The numerical day for this countdown.", true)
                        .addOption(OptionType.STRING, CountdownCommandName.Option.YEAR.get(), "The year for this "
                                + "countdown. Defaults to the upcoming date this month and day fall."),
                new SubcommandData(Subcommand.LIST.get(),
                        "List all the current countdowns for this server."), new SubcommandData(Subcommand.DELETE.get(),
                        "Delete a countdown you created.").addOptions(new OptionData(OptionType.STRING,
                        CountdownCommandName.Option.NAME.get(),
                        "The name of the countdown.", true, true)));
    }

    @Override
    public Class<Subcommand> enumSubcommandClass() {
        return Subcommand.class;
    }
}

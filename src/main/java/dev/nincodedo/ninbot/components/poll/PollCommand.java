package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class PollCommand implements SlashCommand {

    private static final String EXTRA_POLL_CHOICE = "Extra poll choice.";
    private PollScheduler pollScheduler;

    public PollCommand(PollScheduler pollScheduler) {
        this.pollScheduler = pollScheduler;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        if (slashCommandEvent.getMember() == null || slashCommandEvent.getGuild() == null) {
            return messageExecutor;
        }
        Poll poll = parsePollMessage(slashCommandEvent, slashCommandEvent.getMember());
        poll.setResourceBundle(resourceBundle());
        poll.setLocaleString(LocaleService.getLocale(slashCommandEvent.getGuild()).toString());
        slashCommandEvent.reply(poll.build())
                .queue(interactionHook -> interactionHook.retrieveOriginal().queue(message -> {
                    poll.setMessageId(message.getId());
                    pollScheduler.addPoll(poll, slashCommandEvent.getJDA().getShardManager());
                }));
        return messageExecutor;
    }

    Poll parsePollMessage(SlashCommandInteractionEvent slashCommandEvent, Member member) {
        Poll poll = new Poll()
                .setChannelId(slashCommandEvent.getChannel().getId())
                .setServerId(slashCommandEvent.getGuild().getId())
                .setUserAvatarUrl(member.getEffectiveAvatarUrl())
                .setUserName(member.getEffectiveName())
                .setChoices(getPollChoices(slashCommandEvent))
                .setTitle(Objects.requireNonNull(slashCommandEvent.getOption(PollCommandName.Option.QUESTION.get(),
                        OptionMapping::getAsString)));
        //If user choice is set to true, allowed for other users to add their own choices by replying
        var userChoice = slashCommandEvent.getOption(PollCommandName.Option.USERCHOICES.get(), OptionMapping::getAsBoolean);
        poll.setUserChoicesAllowed(Boolean.TRUE.equals(userChoice));
        var time = slashCommandEvent.getOption(PollCommandName.Option.POLLLENGTH.get(), 5L, OptionMapping::getAsLong);
        poll.setEndDateTime(LocalDateTime.now()
                .plus(time, ChronoUnit.MINUTES));
        return poll;
    }

    private List<String> getPollChoices(SlashCommandInteractionEvent slashCommandEvent) {
        List<String> pollChoices = new ArrayList<>();
        pollChoices.add(Objects.requireNonNull(slashCommandEvent.getOption(PollCommandName.Option.CHOICE1.get(),
                OptionMapping::getAsString)));
        pollChoices.add(Objects.requireNonNull(slashCommandEvent.getOption(PollCommandName.Option.CHOICE2.get(),
                OptionMapping::getAsString)));
        pollChoices.addAll(slashCommandEvent.getOptions()
                .stream()
                .filter(Objects::nonNull)
                .filter(optionMapping -> optionMapping.getName().contains(PollCommandName.Option.CHOICE.get()) &&
                        !optionMapping.getName().contains("1") && !optionMapping.getName().contains("2")
                        && !optionMapping.getName().contains("user"))
                .map(OptionMapping::getAsString)
                .toList());
        return pollChoices;
    }

    @Override
    public String getName() {
        return PollCommandName.POLL.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(
                new OptionData(OptionType.STRING, PollCommandName.Option.QUESTION.get(), "Poll question.", true),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE1.get(), "First poll choice.", true),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE2.get(), "Second poll choice.", true),
                new OptionData(OptionType.INTEGER, PollCommandName.Option.POLLLENGTH.get(), "Poll time length in "
                        + "minutes. (Defaults to 5.)", false, true).setMinValue(0).setMaxValue(1440),
                new OptionData(OptionType.BOOLEAN, PollCommandName.Option.USERCHOICES.get(),
                        "Allow other users to add their own choices. (Defaults to false. Must have less than 9 "
                                + "choices.)"),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE3.get(), EXTRA_POLL_CHOICE),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE4.get(), EXTRA_POLL_CHOICE),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE5.get(), EXTRA_POLL_CHOICE),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE6.get(), EXTRA_POLL_CHOICE),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE7.get(), EXTRA_POLL_CHOICE),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE8.get(), EXTRA_POLL_CHOICE),
                new OptionData(OptionType.STRING, PollCommandName.Option.CHOICE9.get(), EXTRA_POLL_CHOICE)
        );
    }
}

package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.LocaleService;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PollCommand implements SlashCommand {

    private PollScheduler pollScheduler;

    public PollCommand(PollScheduler pollScheduler) {
        this.pollScheduler = pollScheduler;
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        Poll poll = parsePollMessage(slashCommandEvent, slashCommandEvent.getMember());
        poll.setResourceBundle(resourceBundle());
        poll.setLocaleString(LocaleService.getLocale(slashCommandEvent.getGuild()).toString());
        slashCommandEvent.reply(poll.build())
                .queue(interactionHook -> {
                    poll.setMessageId(interactionHook.getInteraction().getId());
                    pollScheduler.addPoll(poll, slashCommandEvent.getJDA().getShardManager());
                });
    }

    Poll parsePollMessage(SlashCommandEvent slashCommandEvent, Member member) {
        Poll poll = new Poll()
                .setChannelId(slashCommandEvent.getTextChannel().getId())
                .setServerId(slashCommandEvent.getGuild().getId())
                .setUserAvatarUrl(member.getUser().getAvatarUrl())
                .setUserName(member.getEffectiveName())
                .setChoices(getPollChoices(slashCommandEvent))
                .setTitle(Objects.requireNonNull(slashCommandEvent.getOption("question")).getAsString());
        //If user choice is set to true, allowed for other users to add their own choices by replying
        var userChoiceOption = slashCommandEvent.getOption("user-choices");
        poll.setUserChoicesAllowed(userChoiceOption != null && userChoiceOption.getAsBoolean());
        var timeOption = slashCommandEvent.getOption("poll-length");
        poll.setEndDateTime(LocalDateTime.now()
                .plus(timeOption != null ? timeOption.getAsLong() : 5, ChronoUnit.MINUTES));
        return poll;
    }

    private List<String> getPollChoices(SlashCommandEvent slashCommandEvent) {
        List<String> pollChoices = new ArrayList<>();
        pollChoices.add(Objects.requireNonNull(slashCommandEvent.getOption("choice1")).getAsString());
        pollChoices.add(Objects.requireNonNull(slashCommandEvent.getOption("choice2")).getAsString());
        pollChoices.addAll(slashCommandEvent.getOptions()
                .stream()
                .filter(Objects::nonNull)
                .filter(optionMapping -> optionMapping.getName().contains("choice") && (
                        !optionMapping.getName().contains("1") || !optionMapping.getName().contains("2")))
                .map(OptionMapping::getAsString)
                .collect(Collectors.toList()));
        return pollChoices;
    }

    @Override
    public String getName() {
        return "poll";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(
                new OptionData(OptionType.STRING, "question", "Poll question.", true),
                new OptionData(OptionType.STRING, "choice1", "First poll choice.", true),
                new OptionData(OptionType.STRING, "choice2", "Second poll choice.", true),
                new OptionData(OptionType.INTEGER, "poll-length", "Poll time length in minutes. (Defaults to 5.)"),
                new OptionData(OptionType.BOOLEAN, "user-choices", "Allow other users to add their own choices. "
                        + "(Defaults to false.)"),
                new OptionData(OptionType.STRING, "choice3", "Extra poll choice."),
                new OptionData(OptionType.STRING, "choice4", "Extra poll choice."),
                new OptionData(OptionType.STRING, "choice5", "Extra poll choice."),
                new OptionData(OptionType.STRING, "choice6", "Extra poll choice."),
                new OptionData(OptionType.STRING, "choice7", "Extra poll choice."),
                new OptionData(OptionType.STRING, "choice8", "Extra poll choice."),
                new OptionData(OptionType.STRING, "choice9", "Extra poll choice.")
        );
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }
}

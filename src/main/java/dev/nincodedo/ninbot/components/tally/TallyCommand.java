package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.ninbot.common.command.Subcommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class TallyCommand implements SlashCommand, Subcommand<TallyCommandName.Subcommand> {

    private static HashMap<String, Integer> tallyCount = new HashMap<>();

    @Override
    public String getName() {
        return TallyCommandName.TALLY.get();
    }

    @Override
    public String getDescription() {
        return "Count something, I dunno";
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(new SubcommandData(TallyCommandName.Subcommand.ADD.get(), "Add to the thing you're "
                        + "tallying.")
                        .addOptions(Arrays.asList(
                                new OptionData(OptionType.STRING, TallyCommandName.Option.NAME.get(), "Name of the "
                                        + "thing you're tallying.", true)
                                , new OptionData(OptionType.INTEGER, TallyCommandName.Option.COUNT.get(),
                                        "How many you want to add to the tally."
                                                + " Defaults to 1.", false))),
                new SubcommandData(TallyCommandName.Subcommand.GET.get(), "Gets the current tally count.")
                        .addOption(OptionType.STRING, TallyCommandName.Option.NAME.get(), "Name of the thing you're "
                                + "tallying."));
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var subcommand = slashCommandEvent.getSubcommandName();
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        if (subcommand == null) {
            return messageExecutor;
        }
        switch (getSubcommand(subcommand)) {
            case ADD -> addToTally(slashCommandEvent.getOption(TallyCommandName.Option.COUNT.get()),
                    slashCommandEvent.getOption(TallyCommandName.Option.NAME.get(), OptionMapping::getAsString)
                            .toLowerCase(), messageExecutor);
            case GET ->
                    getTallyCount(slashCommandEvent.getOption(TallyCommandName.Option.NAME.get(),
                                    OptionMapping::getAsString)
                            .toLowerCase(), messageExecutor);
        }
        return messageExecutor;
    }

    private void getTallyCount(String name, SlashCommandEventMessageExecutor messageExecutor) {
        var currentCount = tallyCount.get(name);
        if (currentCount == null) {
            messageExecutor.addEphemeralMessage("No count for " + name);
        } else {
            messageExecutor.addMessageResponse(name + " count: " + currentCount);
        }
    }

    private void addToTally(OptionMapping optionalCount, String name,
            MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor) {
        var count = optionalCount == null ? 1 : (int) optionalCount.getAsLong();
        tallyCount.putIfAbsent(name, 0);
        tallyCount.put(name, tallyCount.get(name) + count);
        int currentCount = tallyCount.get(name);
        messageExecutor.addMessageResponse(name + " count: " + currentCount);
    }

    @Override
    public Class<TallyCommandName.Subcommand> enumSubcommandClass() {
        return TallyCommandName.Subcommand.class;
    }
}

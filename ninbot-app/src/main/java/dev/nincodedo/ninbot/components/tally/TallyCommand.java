package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.ninbot.components.tally.TallyCommandName.Subcommand;
import dev.nincodedo.nincord.command.slash.SlashSubCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import lombok.Getter;
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
public class TallyCommand implements SlashSubCommand<Subcommand> {

    @Getter
    private static HashMap<String, Integer> tallyCount = new HashMap<>();

    @Override
    public String getName() {
        return TallyCommandName.TALLY.get();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(
                new SubcommandData(Subcommand.ADD.get(), "Add to the thing you're tallying.")
                        .addOptions(Arrays.asList(new OptionData(OptionType.STRING,
                                        TallyCommandName.Option.NAME.get(), "Name of the thing you're tallying.", true),
                                new OptionData(OptionType.INTEGER, TallyCommandName.Option.COUNT.get(), "How many you"
                                        + " want to add to the tally. Defaults to 1.", false))),
                new SubcommandData(Subcommand.GET.get(), "Gets the current tally count.")
                        .addOption(OptionType.STRING, TallyCommandName.Option.NAME.get(), "Name of the thing you're "
                                + "tallying."));
    }

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor, @NotNull Subcommand subcommand) {
        switch (subcommand) {
            case ADD -> addToTally(event.getOption(TallyCommandName.Option.COUNT.get()),
                    event.getOption(TallyCommandName.Option.NAME.get(), OptionMapping::getAsString)
                            .toLowerCase(), messageExecutor);
            case GET -> getTallyCount(event.getOption(TallyCommandName.Option.NAME.get(), OptionMapping::getAsString)
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

    private void addToTally(OptionMapping optionalCount, String name, MessageExecutor messageExecutor) {
        var count = optionalCount == null ? 1 : (int) optionalCount.getAsLong();
        tallyCount.putIfAbsent(name, 0);
        tallyCount.put(name, tallyCount.get(name) + count);
        int currentCount = tallyCount.get(name);
        messageExecutor.addMessageResponse(name + " count: " + currentCount);
    }

    @Override
    public Class<Subcommand> enumSubcommandClass() {
        return Subcommand.class;
    }
}

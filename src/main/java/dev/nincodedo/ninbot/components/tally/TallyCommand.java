package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class TallyCommand implements SlashCommand {

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
    public void executeCommandAction(SlashCommandEvent slashCommandEvent) {
        var subcommand = slashCommandEvent.getSubcommandName();
        if (subcommand == null) {
            return;
        }
        switch (TallyCommandName.Subcommand.valueOf(subcommand.toUpperCase())) {
            case ADD -> addToTally(slashCommandEvent);
            case GET -> getTallyCount(slashCommandEvent);
        }
    }

    private void getTallyCount(SlashCommandEvent slashCommandEvent) {
        String name = slashCommandEvent.getOption(TallyCommandName.Option.NAME.get()).getAsString().toLowerCase();
        Integer currentCount = tallyCount.get(name);
        if (currentCount == null) {
            slashCommandEvent.reply("No count for " + name).setEphemeral(true).queue();
        } else {
            slashCommandEvent.reply(name + " count: " + currentCount).queue();
        }
    }

    private void addToTally(SlashCommandEvent slashCommandEvent) {
        String name = slashCommandEvent.getOption(TallyCommandName.Option.NAME.get()).getAsString().toLowerCase();
        OptionMapping optionMapping = slashCommandEvent.getOption(TallyCommandName.Option.COUNT.get());
        Integer count = optionMapping == null ? 1 : (int) optionMapping.getAsLong();
        tallyCount.putIfAbsent(name, 0);
        tallyCount.put(name, tallyCount.get(name) + count);
        int currentCount = tallyCount.get(name);
        slashCommandEvent.reply(name + " count: " + currentCount).queue();
    }
}

package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.ninbot.components.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
public class TallyCommand implements SlashCommand {

    private static HashMap<String, Integer> tallyCount = new HashMap<>();

    @Override
    public String getName() {
        return "tally";
    }

    @Override
    public String getDescription() {
        return "Count something, I dunno";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Collections.emptyList();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(new SubcommandData("add", "Add to the thing you're tallying.").addOptions(Arrays.asList(new OptionData(OptionType.STRING, "name", "Name of the thing you're tallying.", true), new OptionData(OptionType.INTEGER, "count", "How many you want to add to the tally. Defaults to 1.", false))),
                new SubcommandData("get", "Gets the current tally count.").addOption(OptionType.STRING, "name", "Name"
                        + " of the thing you're tallying.", true));
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        switch (slashCommandEvent.getSubcommandName()) {
            case "add" -> addToTally(slashCommandEvent);
            case "get" -> getTallyCount(slashCommandEvent);
            default -> slashCommandEvent.reply("dude what?").setEphemeral(true).queue();
        }
    }

    private void getTallyCount(SlashCommandEvent slashCommandEvent) {
        String name = slashCommandEvent.getOption("name").getAsString().toLowerCase();
        Integer currentCount = tallyCount.get(name);
        if (currentCount == null) {
            slashCommandEvent.reply("No count for " + name).setEphemeral(true).queue();
        } else {
            slashCommandEvent.reply(name + " count: " + currentCount).queue();
        }
    }

    private void addToTally(SlashCommandEvent slashCommandEvent) {
        String name = slashCommandEvent.getOption("name").getAsString().toLowerCase();
        OptionMapping optionMapping = slashCommandEvent.getOption("count");
        Integer count = optionMapping == null ? 1 : (int) optionMapping.getAsLong();
        tallyCount.putIfAbsent(name, 0);
        tallyCount.put(name, tallyCount.get(name) + count);
        int currentCount = tallyCount.get(name);
        slashCommandEvent.reply(name + " count: " + currentCount).queue();
    }
}

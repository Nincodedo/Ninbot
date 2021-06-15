package dev.nincodedo.ninbot.components.tally;

import dev.nincodedo.ninbot.components.command.CommandOption;
import dev.nincodedo.ninbot.components.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class Tally implements SlashCommand {

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
    public List<CommandOption> getCommandOptions() {
        return Arrays.asList(new CommandOption(OptionType.STRING, "name", "Name of the thing you're tallying", true));
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        String name = slashCommandEvent.getOptionsByName("name").get(0).getAsString().toLowerCase();
        tallyCount.putIfAbsent(name, 0);
        tallyCount.put(name, tallyCount.get(name) + 1);
        int count = tallyCount.get(name);
        slashCommandEvent.reply(name + " count: " + count).queue();
    }
}

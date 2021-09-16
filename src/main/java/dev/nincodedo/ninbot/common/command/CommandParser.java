package dev.nincodedo.ninbot.common.command;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class CommandParser {

    private Map<String, SlashCommand> slashCommandMap = new HashMap<>();
    private ExecutorService executorService;

    CommandParser() {
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("command-parser"));
    }

    public void parseEvent(SlashCommandEvent slashCommandEvent) {
        SlashCommand slashCommand = slashCommandMap.get(slashCommandEvent.getName());
        if (slashCommand != null) {
            slashCommand.execute(slashCommandEvent);
        }
    }

    public void addSlashCommands(List<SlashCommand> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
    }

    public void addSlashCommand(SlashCommand slashCommand) {
        slashCommandMap.put(slashCommand.getName(), slashCommand);
    }
}

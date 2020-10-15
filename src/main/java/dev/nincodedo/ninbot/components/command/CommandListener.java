package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private LocaleService localeService;
    private CommandParser commandParser;
    private ComponentService componentService;
    private ConfigService configService;

    public CommandListener(CommandParser commandParser, List<AbstractCommand> commands,
            ComponentService componentService, ConfigService configService, LocaleService localeService) {
        this.commandParser = commandParser;
        this.componentService = componentService;
        this.configService = configService;
        this.localeService = localeService;
        addCommands(commands);
    }

    private void addCommands(List<AbstractCommand> commands) {
        commandParser.registerAliases(commands);
        commandParser.addCommands(commands);
        commandParser.addCommand(new HelpCommand(commandParser.getCommandHashMap(), componentService, configService,
                localeService));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isNinbotMention(event)) {
            commandParser.parseEvent(event);
        }
    }

    private boolean isNinbotMention(MessageReceivedEvent event) {
        return !event.getAuthor().isBot()
                && event.getMessage().getContentStripped().toLowerCase().startsWith("@ninbot");
    }
}

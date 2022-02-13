package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.common.command.InfoCommand;
import org.springframework.stereotype.Component;

@Component
public class NinbotInfoCommand extends InfoCommand {
    public NinbotInfoCommand(NinbotBotInfo ninbotBotInfo) {
        super(ninbotBotInfo);
    }
}

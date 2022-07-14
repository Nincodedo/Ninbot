package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.common.command.slash.info.InfoCommand;
import org.springframework.stereotype.Component;

@Component
public class NinbotInfoCommandImpl extends InfoCommand {
    public NinbotInfoCommandImpl(NinbotBotInfo ninbotBotInfo) {
        super(ninbotBotInfo);
    }
}

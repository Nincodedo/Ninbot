package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.nincord.command.slash.info.InfoCommand;
import org.springframework.stereotype.Component;

@Component
public class NinbotInfoCommandImpl extends InfoCommand {
    public NinbotInfoCommandImpl(NinbotBotInfo ninbotBotInfo) {
        super(ninbotBotInfo);
    }
}

package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class AutoCompleteCommandMessageExecutor extends MessageExecutor<AutoCompleteCommandMessageExecutor> {

    private CommandAutoCompleteInteractionEvent event;

    public AutoCompleteCommandMessageExecutor(@NotNull CommandAutoCompleteInteractionEvent event) {
        super();
        this.event = event;
    }

    @Override
    public void executeMessageActions() {
        // no message actions occur for auto complete
    }

    @Override
    public AutoCompleteCommandMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return event.getMessageChannel();
    }

    @Override
    public Message getMessage() {
        return null;
    }
}

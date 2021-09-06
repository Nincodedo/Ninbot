package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashCommandEventMessageExecutor extends MessageExecutor<SlashCommandEventMessageExecutor> {

    private SlashCommandEvent slashCommandEvent;

    public SlashCommandEventMessageExecutor(SlashCommandEvent slashCommandEvent) {
        super();
        this.slashCommandEvent = slashCommandEvent;
    }

    public void executeActions() {
        super.executeActions();
    }

    @Override
    public SlashCommandEventMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return slashCommandEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return overrideMessage;
    }
}

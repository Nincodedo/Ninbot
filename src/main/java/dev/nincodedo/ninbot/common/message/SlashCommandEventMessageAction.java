package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashCommandEventMessageAction extends MessageAction<SlashCommandEventMessageAction> {

    private SlashCommandEvent slashCommandEvent;

    public SlashCommandEventMessageAction(SlashCommandEvent slashCommandEvent) {
        super();
        this.slashCommandEvent = slashCommandEvent;
    }

    @Override
    public void executeActions() {
        //map all the emote reactions to RestActions
        Stream<RestAction<Void>> reactionEmoteStream = reactionEmotes
                .stream()
                .map(emote -> overrideMessage.addReaction(emote));
        //map all the emoji reactions to RestActions
        Stream<RestAction<Void>> reactionStream = reactions
                .stream()
                .map(stringEmote -> overrideMessage.addReaction(stringEmote));
        //Combine them into one large RestAction and queue it
        RestAction.allOf(Stream.concat(reactionStream, reactionEmoteStream)
                .collect(Collectors.toList())).queue();
    }

    @Override
    public SlashCommandEventMessageAction returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return slashCommandEvent.getChannel();
    }
}

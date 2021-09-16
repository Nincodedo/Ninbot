package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.Emojis;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public abstract class MessageExecutor<T> {

    List<String> reactions;
    List<Emote> reactionEmotes;
    Message overrideMessage;

    public MessageExecutor() {
        reactions = new ArrayList<>();
        reactionEmotes = new ArrayList<>();
    }

    public void executeActions() {
        //map all the emote reactions to RestActions
        if (getMessage() != null) {
            Stream<RestAction<Void>> reactionEmoteStream = reactionEmotes
                    .stream()
                    .map(emote -> getMessage().addReaction(emote));
            //map all the emoji reactions to RestActions
            Stream<RestAction<Void>> reactionStream = reactions
                    .stream()
                    .map(stringEmote -> getMessage().addReaction(stringEmote));
            //Combine them into one large RestAction and queue it
            RestAction.allOf(Stream.concat(reactionStream, reactionEmoteStream)
                    .toList()).queue();
        }
    }

    public abstract T returnThis();

    public T addCorrectReaction(boolean isSuccessful) {
        if (isSuccessful) {
            addSuccessfulReaction();
        } else {
            addUnsuccessfulReaction();
        }
        return returnThis();
    }

    public T addUnknownReaction() {
        addReaction(Emojis.QUESTION_MARK);
        return returnThis();
    }

    public T addSuccessfulReaction() {
        addReaction(Emojis.CHECK_MARK);
        return returnThis();
    }

    public T addUnsuccessfulReaction() {
        addReaction(Emojis.CROSS_X);
        return returnThis();
    }

    public T addReaction(String... emoji) {
        reactions.addAll(Arrays.asList(emoji));
        return returnThis();
    }

    public T addReaction(List<String> emoji) {
        reactions.addAll(emoji);
        return returnThis();
    }


    public T setOverrideMessage(Message message) {
        this.overrideMessage = message;
        return returnThis();
    }

    public T addReactionEmotes(List<Emote> emotes) {
        reactionEmotes.addAll(emotes);
        return returnThis();
    }

    public abstract MessageChannel getChannel();

    public abstract Message getMessage();
}

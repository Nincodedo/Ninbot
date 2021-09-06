package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.Emojis;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class MessageAction<T> {

    List<String> reactions;
    List<Emote> reactionEmotes;
    Message overrideMessage;

    public MessageAction() {
        reactions = new ArrayList<>();
        reactionEmotes = new ArrayList<>();
    }

    public abstract void executeActions();

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
}

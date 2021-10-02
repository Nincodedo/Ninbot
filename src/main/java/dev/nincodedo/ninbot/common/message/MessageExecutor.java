package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.Emojis;
import lombok.Getter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class MessageExecutor<T> {

    List<String> reactions;
    List<Emote> reactionEmotes;
    Message overrideMessage;
    List<Message> messageResponses;

    public MessageExecutor() {
        reactions = new ArrayList<>();
        reactionEmotes = new ArrayList<>();
        messageResponses = new ArrayList<>();
    }

    public void executeActions() {
        //map all the emote reactions to RestActions
        if (getMessage() != null) {
            List<RestAction<Void>> reactionRestActionList = new ArrayList<>();
            var reactionEmoteList = reactionEmotes
                    .stream()
                    .map(emote -> getMessage().addReaction(emote)).toList();
            if (reactionEmoteList != null) {
                reactionRestActionList.addAll(reactionEmoteList);
            }
            //map all the emoji reactions to RestActions
            var reactionList = reactions.stream()
                    .map(stringEmote -> getMessage().addReaction(stringEmote))
                    .toList();
            if (reactionList != null) {
                reactionRestActionList.addAll(reactionList);
            }
            //Combine them into one large RestAction and queue it
            RestAction.allOf(reactionRestActionList).queue();
        }
        executeMessageActions();
    }

    public abstract void executeMessageActions();

    public abstract T returnThis();

    public T addMessageResponse(Message message) {
        messageResponses.add(message);
        return returnThis();
    }

    public T addMessageResponse(String message) {
        messageResponses.add(new MessageBuilder().append(message).build());
        return returnThis();
    }

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

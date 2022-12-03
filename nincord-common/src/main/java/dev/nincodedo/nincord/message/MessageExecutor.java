package dev.nincodedo.nincord.message;

import dev.nincodedo.nincord.Emojis;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class MessageExecutor {

    List<String> reactions;
    List<Emoji> reactionEmotes;
    Message overrideMessage;
    List<MessageCreateData> messageResponses;

    protected MessageExecutor() {
        reactions = new ArrayList<>();
        reactionEmotes = new ArrayList<>();
        messageResponses = new ArrayList<>();
    }

    public void executeActions() {
        //map all the emote reactions to RestActions
        if (getMessage() != null) {
            var reactionEmoteList = reactionEmotes
                    .stream()
                    .map(emote -> getMessage().addReaction(emote)).toList();
            List<RestAction<Void>> reactionRestActionList = new ArrayList<>(reactionEmoteList);
            //map all the emoji reactions to RestActions
            var reactionList = reactions.stream()
                    .map(stringEmote -> getMessage().addReaction(Emoji.fromFormatted(stringEmote)))
                    .toList();
            reactionRestActionList.addAll(reactionList);
            //Combine them into one large RestAction and queue it
            if (!reactionRestActionList.isEmpty()) {
                RestAction.allOf(reactionRestActionList).queue();
            }
        }
        executeMessageActions();
    }

    public abstract void executeMessageActions();

    public void addMessageResponse(MessageCreateData message) {
        messageResponses.add(message);
    }

    public void addMessageResponse(String message) {
        messageResponses.add(new MessageCreateBuilder().addContent(message).build());
    }

    public void addUnsuccessfulReaction() {
        addReaction(Emojis.CROSS_X);
    }

    public void addReaction(String... emoji) {
        reactions.addAll(Arrays.asList(emoji));
    }

    public void addReaction(List<String> emoji) {
        reactions.addAll(emoji);
    }


    public void setOverrideMessage(Message message) {
        this.overrideMessage = message;
    }

    public void addReactionEmotes(List<RichCustomEmoji> emotes) {
        reactionEmotes.addAll(emotes);
    }

    public abstract MessageChannel getChannel();

    public abstract Message getMessage();
}

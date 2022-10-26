package dev.nincodedo.ninbot.components.reaction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class EmojiReactionResponse extends ReactionResponse {

    @Getter
    private List<String> emojiList;

    public EmojiReactionResponse(ReactionResponse reactionResponse) {
        super(reactionResponse);
        addEmojis();
    }

    public EmojiReactionResponse(String response) {
        this.response = response;
        addEmojis();
    }

    private String getLetterEmoji(char c) {
        return ReactionUtils.getLetterMap().get(Character.toString(c).toUpperCase());
    }

    private void addEmojis() {
        emojiList = new ArrayList<>();
        for (char c : response.toCharArray()) {
            emojiList.add(getLetterEmoji(c));
        }
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        List<RestAction<Void>> restActions = new ArrayList<>();
        emojiList.forEach(emoji -> restActions.add(message.addReaction(Emoji.fromFormatted(emoji))));
        RestAction.allOf(restActions).queue();
    }
}

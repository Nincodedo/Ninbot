package com.nincraft.ninbot.components.leaderboard;

import com.nincraft.ninbot.components.common.Emojis;
import lombok.val;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class ReactionResultListener extends ListenerAdapter {

    private String recordType;
    private String messageId;
    private String firstUser;
    private String againstUser;
    private LeaderboardService leaderboardService;

    ReactionResultListener(LeaderboardService leaderboardService,
            String recordType, String messageId, String firstUser, String againstUser) {
        this.recordType = recordType;
        this.messageId = messageId;
        this.firstUser = firstUser;
        this.againstUser = againstUser;
        this.leaderboardService = leaderboardService;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot() && !firstUser.equals(event.getUser().getId())
                && event.getMessageId().equals(messageId)) {
            if (event.getUser().getId().equals(againstUser)) {
                val emote = event.getReactionEmote().getName();
                if (Emojis.CHECK_MARK.equals(emote)) {
                    leaderboardService.recordResult(event.getGuild().getId(), recordType, firstUser, againstUser);
                    clearReactions(event);
                    event.getJDA().removeEventListener(this);
                } else if (Emojis.CROSS_X.equals(emote)) {
                    clearReactions(event);
                    event.getJDA().removeEventListener(this);
                } else {
                    event.getReaction().removeReaction().queue();
                }
            } else {
                event.getReaction().removeReaction().queue();
            }
        }
    }

    private void clearReactions(MessageReactionAddEvent event) {
        event.getChannel().retrieveMessageById(event.getMessageId()).complete().clearReactions().queue();
    }
}

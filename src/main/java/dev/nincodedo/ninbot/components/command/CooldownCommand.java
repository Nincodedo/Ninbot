package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.common.Constants;
import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.StreamUtils;
import dev.nincodedo.ninbot.common.message.MessageAction;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class CooldownCommand extends AbstractCommand {

    protected ChronoUnit cooldownUnit;
    protected int cooldownValue;
    protected Map<String, Date> cooldownMap = new HashMap<>();

    protected MessageAction executeCommand(MessageReceivedEvent event) {
        var lastTimeCommandUsed = cooldownMap.get(name);
        if (lastTimeCommandUsed != null && Instant.now()
                .minus(cooldownValue, cooldownUnit)
                .isBefore(lastTimeCommandUsed.toInstant())) {
            return new MessageAction(event).addReaction(Emojis.COOLDOWN_5_CLOCK);
        } else {
            var loadingEmote = event.getJDA()
                    .getShardManager()
                    .getEmotesByName("loading", true)
                    .stream()
                    .filter(emote -> emote.getGuild().getId().equals(Constants.NINBOT_SERVER_ID))
                    .min(StreamUtils.shuffle());
            var message = event.getMessage();
            cooldownMap.put(name, new Date());
            loadingEmote.ifPresent(emote -> message.addReaction(emote)
                    .queue(avoid -> message.removeReaction(emote).queueAfter(5L, TimeUnit.of(ChronoUnit.SECONDS))));
            return executeCooldownCommand(event);
        }
    }

    protected abstract MessageAction executeCooldownCommand(MessageReceivedEvent event);
}

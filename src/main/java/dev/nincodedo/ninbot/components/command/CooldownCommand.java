package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.Constants;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.common.StreamUtils;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;
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
        val lastTimeCommandUsed = cooldownMap.get(name);
        if (lastTimeCommandUsed != null && Instant.now()
                .minus(cooldownValue, cooldownUnit)
                .isBefore(lastTimeCommandUsed.toInstant())) {
            return new MessageAction(event).addReaction(Emojis.COOLDOWN_5_CLOCK);
        } else {
            val loadingEmote = event.getJDA()
                    .getShardManager()
                    .getEmotesByName("loading", true)
                    .stream()
                    .filter(emote -> emote.getGuild().getId().equals(Constants.NINBOT_SERVER_ID))
                    .sorted(StreamUtils.shuffle())
                    .findFirst();
            val message = event.getMessage();
            cooldownMap.put(name, new Date());
            loadingEmote.ifPresent(emote -> message.addReaction(emote)
                    .queue(avoid -> message.removeReaction(emote).queueAfter(5L, TimeUnit.of(ChronoUnit.SECONDS))));
            return executeCooldownCommand(event);
        }
    }

    protected abstract MessageAction executeCooldownCommand(MessageReceivedEvent event);
}

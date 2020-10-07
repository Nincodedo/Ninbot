package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
            val loadingEmote = event.getJDA().getShardManager().getEmoteById(Emojis.PACMAN_LOADING_ID);
            val message = event.getMessage();
            cooldownMap.put(name, new Date());
            message.addReaction(loadingEmote).delay(Duration.ofSeconds(10)).flatMap(
                    avoid -> message.removeReaction(loadingEmote)
            );

            return executeCooldownCommand(event);
        }
    }

    protected abstract MessageAction executeCooldownCommand(MessageReceivedEvent event);
}

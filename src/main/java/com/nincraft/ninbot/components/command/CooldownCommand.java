package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.MessageAction;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
            cooldownMap.put(name, new Date());
            return executeCooldownCommand(event);
        }
    }

    protected abstract MessageAction executeCooldownCommand(MessageReceivedEvent event);
}

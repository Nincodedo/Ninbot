package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.Reference;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class CommandParser {

    @Getter
    private static Map<String, AbstractCommand> commandHashMap = new HashMap<>();

    void parseEvent(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (StringUtils.isNotBlank(message)) {
            AbstractCommand command = commandHashMap.get(getCommand(message));
            if (command != null) {
                try {
                    command.execute(event);
                } catch (Exception e) {
                    log.error("Error executing command " + command.getName(), e);
                    MessageUtils.sendMessage(getChannel(event.getJDA(), Reference.OCW_DEBUG_CHANNEL), e.toString() +
                            "\n" + e.getStackTrace()[0].toString());
                }
            } else {
                MessageUtils.reactUnknownResponse(event.getMessage());
            }
        }
    }

    private MessageChannel getChannel(JDA jda, String channel) {
        return jda.getGuildById(Reference.OCW_SERVER_ID).getTextChannelById(channel);
    }

    private String getCommand(String message) {
        String[] splitMessage = message.split(" ");
        if (splitMessage.length > 1) {
            return splitMessage[1] != null ? splitMessage[1].toLowerCase() : StringUtils.EMPTY;
        }
        return null;
    }

    void addCommand(AbstractCommand command) {
        commandHashMap.put(command.getName(), command);
    }
}

package dev.nincodedo.ninbot.components.fun.hugemoji;

import com.vdurmont.emoji.EmojiParser;
import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Log4j2
@Component
public class HugemojiCommand extends AbstractCommand {

    public HugemojiCommand() {
        name = "hugemoji";
        length = 3;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val emoteList = event.getMessage().getEmotes();
        val emojiList = EmojiParser.extractEmojis(event.getMessage().getContentStripped());
        if (!emoteList.isEmpty()) {
            try {
                val channel = event.getChannel();
                val emote = emoteList.get(0);
                val imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
                InputStream file = new URL(emote.getImageUrl()).openStream();
                channel.sendFile(file, emote.getName() + imageFileType).queue();
            } catch (IOException e) {
                log.error("Failed to upload emote image", e);
            }
        } else if (!emojiList.isEmpty()) {
            messageAction.addChannelAction(emojiList.get(0));
        } else {
            messageAction.addUnsuccessfulReaction();
        }
        return messageAction;
    }
}

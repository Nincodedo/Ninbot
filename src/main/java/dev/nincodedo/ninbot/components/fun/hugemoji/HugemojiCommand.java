package dev.nincodedo.ninbot.components.fun.hugemoji;

import com.vdurmont.emoji.EmojiParser;
import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class HugemojiCommand extends AbstractCommand {

    private static final org.apache.logging.log4j.Logger log =
            org.apache.logging.log4j.LogManager.getLogger(HugemojiCommand.class);

    public HugemojiCommand() {
        name = "hugemoji";
        length = 3;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        final java.util.List<net.dv8tion.jda.api.entities.Emote> emoteList = event.getMessage().getEmotes();
        final java.util.List<java.lang.String> emojiList = EmojiParser.extractEmojis(event.getMessage()
                .getContentStripped());
        if (!emoteList.isEmpty()) {
            try {
                final net.dv8tion.jda.api.entities.MessageChannel channel = event.getChannel();
                final net.dv8tion.jda.api.entities.Emote emote = emoteList.get(0);
                String imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
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

package dev.nincodedo.ninbot.components.fun.dab;

import dev.nincodedo.ninbot.common.message.MessageAction;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageAction;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Emote;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class HugeDabCommand extends DabCommand {
    public HugeDabCommand(GitProperties gitProperties) {
        super(gitProperties);
    }

    @Override
    public String getName() {
        return "hugedab";
    }

    @Override
    void sendDabs(MessageAction<SlashCommandEventMessageAction> messageAction, List<Emote> emoteList) {
        try {
            var channel = messageAction.getChannel();
            var emote = emoteList.get(new Random().nextInt(emoteList.size()));
            var imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
            InputStream file = new URL(emote.getImageUrl()).openStream();
            channel.sendFile(file, emote.getName() + imageFileType).queue();
        } catch (IOException e) {
            log.error("Failed to upload emote image", e);
        }
    }
}

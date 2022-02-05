package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.message.MessageExecutor;
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
public class HugeDabber extends Dabber {
    public HugeDabber(GitProperties gitProperties) {
        super(gitProperties);
    }

    @Override
    void sendDabs(MessageExecutor messageExecutor, List<Emote> emoteList) {
        try {
            var channel = messageExecutor.getChannel();
            var emote = emoteList.get(new Random().nextInt(emoteList.size()));
            var imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
            InputStream file = new URL(emote.getImageUrl()).openStream();
            channel.sendFile(file, emote.getName() + imageFileType).queue();
        } catch (IOException e) {
            log.error("Failed to upload emote image", e);
        }
    }
}

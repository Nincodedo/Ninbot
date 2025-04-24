package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.supporter.SupporterCheck;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
public class HugeDabber extends Dabber {
    public HugeDabber(GitProperties gitProperties, SupporterCheck supporterCheck) {
        super(gitProperties, supporterCheck);
    }

    @Override
    void sendDabs(MessageExecutor messageExecutor, List<RichCustomEmoji> emoteList) {
        try {
            var channel = messageExecutor.getChannel();
            var emote = emoteList.get(getRandom().nextInt(emoteList.size()));
            var imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
            InputStream file = new URI(emote.getImageUrl()).toURL().openStream();
            channel.sendFiles(FileUpload.fromData(file, emote.getName() + imageFileType)).queue();
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to upload emote image", e);
        }
    }
}

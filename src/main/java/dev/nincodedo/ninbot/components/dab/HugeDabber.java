package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
            InputStream file = new URL(emote.getImageUrl()).openStream();
            channel.sendFiles(FileUpload.fromData(file, emote.getName() + imageFileType)).queue();
        } catch (IOException e) {
            log.error("Failed to upload emote image", e);
        }
    }
}

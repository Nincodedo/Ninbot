package dev.nincodedo.ninbot.components.fun.dab;

import dev.nincodedo.ninbot.components.common.message.MessageAction;
import net.dv8tion.jda.api.entities.Emote;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;

@Component
public class HugeDabCommand extends DabCommand {

    private static final org.apache.logging.log4j.Logger log =
            org.apache.logging.log4j.LogManager.getLogger(HugeDabCommand.class);

    public HugeDabCommand(GitProperties gitProperties) {
        super(gitProperties);
        name = "hugedab";
        length = 3;
    }

    @Override
    void sendDabs(MessageAction messageAction, List<Emote> emoteList) {
        try {
            final net.dv8tion.jda.api.entities.MessageChannel channel = messageAction.getEvent().getChannel();
            final net.dv8tion.jda.api.entities.Emote emote = emoteList.get(new Random().nextInt(emoteList.size()));
            String imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
            InputStream file = new URL(emote.getImageUrl()).openStream();
            channel.sendFile(file, emote.getName() + imageFileType).queue();
        } catch (IOException e) {
            log.error("Failed to upload emote image", e);
        }
    }
}

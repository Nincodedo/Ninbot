package dev.nincodedo.ninbot.components.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class UnarchiveChannelCommand extends ArchiveChannelCommand {
    public UnarchiveChannelCommand() {
        super();
        length = 3;
        checkExactLength = true;
        name = "unarchive";
    }

    @Override
    String getCategoryIdMovingTo(MessageReceivedEvent event, Guild guild) {
        var parentCategory = event.getTextChannel().getParent();
        if (parentCategory != null) {
            return parentCategory.getId();
        } else {
            return null;
        }
    }
}

package dev.nincodedo.nincord.message;

import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.TimerTask;

public class GenericAnnounce extends TimerTask {

    private ShardManager shardManager;
    private String announcementChannelId;
    private String announceString;
    private MessageCreateData announceMessage;

    public GenericAnnounce(ShardManager shardManager, String announcementChannelId, String announceString) {
        this.shardManager = shardManager;
        this.announcementChannelId = announcementChannelId;
        this.announceString = announceString;
    }

    public GenericAnnounce(ShardManager shardManager, String announcementChannelId, MessageCreateData announceMessage) {
        this.shardManager = shardManager;
        this.announcementChannelId = announcementChannelId;
        this.announceMessage = announceMessage;
    }

    @Override
    public void run() {
        var guildChannel = shardManager.getGuildChannelById(announcementChannelId);
        if (!(guildChannel instanceof GuildMessageChannelUnion channel)) {
            return;
        }
        if (announceString != null) {
            channel.sendMessage(announceString).queue();
        } else if (announceMessage != null) {
            channel.sendMessage(announceMessage).queue();
        }
    }
}

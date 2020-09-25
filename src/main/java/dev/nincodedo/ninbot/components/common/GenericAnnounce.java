package dev.nincodedo.ninbot.components.common;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.TimerTask;

public class GenericAnnounce extends TimerTask {

    private ShardManager shardManager;
    private String announcementChannelId;
    private String announceString;
    private Message announceMessage;

    public GenericAnnounce(ShardManager shardManager, String announcementChannelId, String announceString) {
        this.shardManager = shardManager;
        this.announcementChannelId = announcementChannelId;
        this.announceString = announceString;
    }

    public GenericAnnounce(ShardManager shardManager, String announcementChannelId, Message announceMessage) {
        this.shardManager = shardManager;
        this.announcementChannelId = announcementChannelId;
        this.announceMessage = announceMessage;
    }

    @Override
    public void run() {
        if (announceString != null) {
            shardManager.getTextChannelById(announcementChannelId).sendMessage(announceString).queue();
        } else if (announceMessage != null) {
            shardManager.getTextChannelById(announcementChannelId).sendMessage(announceMessage).queue();
        }
    }
}

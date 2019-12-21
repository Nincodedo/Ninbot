package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.TimerTask;

public class GenericAnnounce extends TimerTask {

    private ShardManager shardManager;
    private String announceChannel;
    private String announceString;
    private Message announceMessage;

    public GenericAnnounce(ShardManager shardManager, String announceChannel, String announceString) {
        this.shardManager = shardManager;
        this.announceChannel = announceChannel;
        this.announceString = announceString;
    }

    public GenericAnnounce(ShardManager shardManager, String announceChannel, Message announceMessage) {
        this.shardManager = shardManager;
        this.announceChannel = announceChannel;
        this.announceMessage = announceMessage;
    }

    @Override
    public void run() {
        if (announceString != null) {
            shardManager.getTextChannelById(announceChannel).sendMessage(announceString).queue();
        } else if (announceMessage != null) {
            shardManager.getTextChannelById(announceChannel).sendMessage(announceMessage).queue();
        }
    }
}

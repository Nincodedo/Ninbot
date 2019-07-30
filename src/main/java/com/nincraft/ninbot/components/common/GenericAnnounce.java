package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.TimerTask;

public class GenericAnnounce extends TimerTask {

    private ShardManager shardManager;
    private String announceChannel;
    private String announceMessage;

    public GenericAnnounce(ShardManager shardManager, String announceChannel, String announceMessage) {
        this.shardManager = shardManager;
        this.announceChannel = announceChannel;
        this.announceMessage = announceMessage;
    }

    @Override
    public void run() {
        shardManager.getTextChannelById(announceChannel).sendMessage(announceMessage).queue();
    }
}

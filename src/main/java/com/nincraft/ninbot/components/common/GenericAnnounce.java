package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.api.JDA;

import java.util.TimerTask;

public class GenericAnnounce extends TimerTask {

    private JDA jda;
    private String announceChannel;
    private String announceMessage;

    public GenericAnnounce(JDA jda, String announceChannel, String announceMessage) {
        this.jda = jda;
        this.announceChannel = announceChannel;
        this.announceMessage = announceMessage;
    }

    @Override
    public void run() {
        jda.getTextChannelById(announceChannel).sendMessage(announceMessage).queue();
    }
}

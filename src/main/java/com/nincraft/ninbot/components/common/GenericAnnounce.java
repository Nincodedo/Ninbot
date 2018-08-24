package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

public class GenericAnnounce extends TimerTask {

    private MessageUtils messageUtils;
    private JDA jda;
    private String announceChannel;
    private String announceMessage;

    public GenericAnnounce(JDA jda, MessageUtils messageUtils, String announceChannel, String announceMessage) {
        this.jda = jda;
        this.messageUtils = messageUtils;
        this.announceChannel = announceChannel;
        this.announceMessage = announceMessage;
    }

    @Override
    public void run() {
        messageUtils.sendMessage(jda.getTextChannelById(announceChannel), announceMessage);
    }
}

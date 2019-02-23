package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.time.temporal.TemporalAccessor;

/**
 * A lazy combo of {@link MessageBuilder} and {@link EmbedBuilder}
 */
public class MessageBuilderHelper {

    private MessageBuilder messageBuilder;
    private EmbedBuilder embedBuilder;

    public MessageBuilderHelper() {
        messageBuilder = new MessageBuilder();
        embedBuilder = new EmbedBuilder();
    }

    /**
     * see {@link EmbedBuilder#setTitle(String)}
     */
    public void setTitle(String title) {
        embedBuilder.setTitle(title);
    }

    /**
     * see {@link EmbedBuilder#setColor(Color)}
     */
    public void setColor(Color color) {
        embedBuilder.setColor(color);
    }

    /**
     * see {@link EmbedBuilder#setTimestamp(TemporalAccessor)}
     */
    public void setTimestamp(TemporalAccessor temporal) {
        embedBuilder.setTimestamp(temporal);
    }

    /**
     * see {@link EmbedBuilder#setAuthor(String, String, String)}
     */
    public void setAuthor(String name, String url, String iconUrl) {
        embedBuilder.setAuthor(name, url, iconUrl);
    }

    /**
     * see {@link EmbedBuilder#setAuthor(String)}
     */
    public void setAuthor(String name) {
        embedBuilder.setAuthor(name);
    }

    /**
     * see {@link EmbedBuilder#addField(String, String, boolean)}
     */
    public void addField(String name, String value, boolean inline) {
        embedBuilder.addField(name, value, inline);
    }

    /**
     * see {@link EmbedBuilder#setFooter(String, String)}
     */
    public void setFooter(String footer, String url) {
        embedBuilder.setFooter(footer, url);
    }

    /**
     * see {@link MessageBuilder#build()}
     */
    public Message build() {
        return messageBuilder.setEmbed(embedBuilder.build()).build();
    }

    /**
     * see {@link EmbedBuilder#appendDescription(CharSequence)}
     */
    public void appendDescription(String description) {
        embedBuilder.appendDescription(description);
    }
}

package dev.nincodedo.ninbot.components.stream;

import net.dv8tion.jda.api.EmbedBuilder;

import java.io.File;

public record EmbedFileMessage(EmbedBuilder embedBuilder, File file, String fileName) {
}

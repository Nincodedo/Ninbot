package dev.nincodedo.ninbot.components.fun.hugemoji;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class HugemojiCommand implements SlashCommand {

    @Override
    public void executeCommandAction(@NotNull SlashCommandEvent slashCommandEvent) {
        var possibleEmoteString = slashCommandEvent.getOption(HugemojiCommandName.Option.EMOTE.get())
                .getAsString();
        List<Emote> emoteList = new ArrayList<>();
        List<String> emojiList = new ArrayList<>();
        if (EmojiManager.isEmoji(possibleEmoteString)) {
            emojiList = new ArrayList<>(EmojiParser.extractEmojis(possibleEmoteString));
        } else if (possibleEmoteString.contains(":")) {
            var stringEmote = possibleEmoteString.split(":")[1];
            emoteList = slashCommandEvent.getGuild().getEmotesByName(stringEmote, true);
        }
        if (!emoteList.isEmpty()) {
            try {
                var emote = emoteList.get(0);
                var imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
                InputStream file = new URL(emote.getImageUrl()).openStream();
                slashCommandEvent.replyEmbeds(new EmbedBuilder().setImage(
                                "attachment://" + emote.getName() + imageFileType).build())
                        .addFile(file, emote.getName() + imageFileType)
                        .queue();
            } catch (IOException e) {
                slashCommandEvent.reply(Emojis.CROSS_X).setEphemeral(true).queue();
            }
        } else if (!emojiList.isEmpty()) {
            emojiList.forEach(emoji -> slashCommandEvent.reply(emoji).queue());
        } else {
            slashCommandEvent.reply("This ain't an emote I know about.").setEphemeral(true).queue();
        }
    }

    @Override
    public String getName() {
        return HugemojiCommandName.HUGEMOJI.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(new OptionData(OptionType.STRING, HugemojiCommandName.Option.EMOTE.get(), "The emote to "
                + "biggify.", true));
    }
}

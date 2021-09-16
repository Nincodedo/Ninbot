package dev.nincodedo.ninbot.components.fun.hugemoji;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class HugemojiCommand implements SlashCommand {

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        var stringEmote = slashCommandEvent.getOption(HugemojiCommandName.Option.EMOTE.get())
                .getAsString()
                .split(":")[1];
        var emoteList = slashCommandEvent.getGuild().getEmotesByName(stringEmote, true);
        if (!emoteList.isEmpty()) {
            try {
                var emote = emoteList.get(0);
                var channel = slashCommandEvent.getChannel();
                var imageFileType = emote.getImageUrl().substring(emote.getImageUrl().lastIndexOf('.'));
                InputStream file = new URL(emote.getImageUrl()).openStream();
                channel.sendFile(file, emote.getName() + imageFileType).queue();
                slashCommandEvent.reply(Emojis.CHECK_MARK).setEphemeral(true).queue();
            } catch (IOException e) {
                slashCommandEvent.reply(Emojis.CROSS_X).setEphemeral(true).queue();
            }
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
        return Arrays.asList(new OptionData(OptionType.STRING, HugemojiCommandName.Option.EMOTE.get(), "The emote to biggify.", true));
    }
}

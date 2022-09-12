package dev.nincodedo.ninbot.components.hugemoji;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.Emoji.Type;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Component
public class HugemojiCommand implements SlashCommand {

    private SecureRandom random;

    public HugemojiCommand() {
        random = new SecureRandom();
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(
            @NotNull SlashCommandInteractionEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        var emojiOption = slashCommandEvent.getOption(HugemojiCommandName.Option.EMOTE.get(),
                OptionMapping::getAsString);
        if (emojiOption == null) {
            return messageExecutor;
        }
        var emojiUnion = Emoji.fromFormatted(emojiOption);
        var emojiType = emojiUnion.getType();
        if (emojiType == Type.UNICODE) {
            messageExecutor.addMessageResponse(emojiUnion.asUnicode().getName());
        } else if (emojiType == Type.CUSTOM) {
            var customEmoji = emojiUnion.asCustom();
            BufferedImage image;
            try {
                image = ImageIO.read(new URL(customEmoji.getImageUrl()).openStream());
            } catch (IOException e) {
                log.error("Failed to open custom emoji url {} for custom emoji {}", customEmoji.getImageUrl(),
                        customEmoji.getFormatted(), e);
                messageExecutor.addEphemeralMessage(Emojis.CROSS_X);
                return messageExecutor;
            }
            var imageFileType = customEmoji.getImageUrl().substring(customEmoji.getImageUrl().lastIndexOf('.'));
            InputStream beegImage;
            try {
                beegImage = biggifyImage(image, imageFileType);
            } catch (IOException e) {
                log.error("Failed to enlarge image for custom emoji {}", customEmoji.getFormatted(), e);
                messageExecutor.addEphemeralMessage(Emojis.CROSS_X);
                return messageExecutor;
            }
            slashCommandEvent.deferReply().queue();
            slashCommandEvent.getHook()
                    .editOriginalAttachments(FileUpload.fromData(beegImage, customEmoji.getName() + imageFileType))
                    .queue();
        }
        return messageExecutor;
    }

    private InputStream biggifyImage(BufferedImage image, String imageFileType) throws IOException {
        var beegImage = Scalr.resize(image, random.nextInt(100, 300));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(beegImage, imageFileType.substring(1), os);
        image.flush();
        return new ByteArrayInputStream(os.toByteArray());
    }

    @Override
    public String getName() {
        return HugemojiCommandName.HUGEMOJI.get();
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(new OptionData(OptionType.STRING, HugemojiCommandName.Option.EMOTE.get(), "The emote to "
                + "biggify.", true));
    }
}

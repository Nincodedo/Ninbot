package dev.nincodedo.ninbot.components.hugemoji;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.common.supporter.SupporterCheck;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
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
import java.util.Optional;

@Slf4j
@Component
public class HugemojiCommand implements SlashCommand {

    private SecureRandom random;
    private SupporterCheck supporterCheck;

    public HugemojiCommand(SupporterCheck supporterCheck) {
        random = new SecureRandom();
        this.supporterCheck = supporterCheck;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> execute(
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
            var imageFileType = customEmoji.getImageUrl().substring(customEmoji.getImageUrl().lastIndexOf('.'));
            var optionalInputStream = getImageInputStream(slashCommandEvent, messageExecutor, customEmoji,
                    imageFileType);
            if (optionalInputStream.isPresent()) {
                slashCommandEvent.deferReply().queue();
                slashCommandEvent.getHook()
                        .editOriginalAttachments(FileUpload.fromData(optionalInputStream.get(),
                                customEmoji.getName() + imageFileType))
                        .queue();
            } else {
                messageExecutor.addEphemeralUnsuccessfulReaction();
            }
        }
        return messageExecutor;
    }

    private Optional<InputStream> getImageInputStream(@NotNull SlashCommandInteractionEvent slashCommandEvent,
            SlashCommandEventMessageExecutor messageExecutor, CustomEmoji customEmoji, String imageFileType) {
        InputStream inputStream;
        try {
            inputStream = new URL(customEmoji.getImageUrl()).openStream();
        } catch (IOException e) {
            log.error("Failed to open custom emoji url {} for custom emoji {}", customEmoji.getImageUrl(),
                    customEmoji.getFormatted(), e);
            messageExecutor.addEphemeralUnsuccessfulReaction();
            return Optional.empty();
        }
        if (customEmoji.isAnimated()) {
            return Optional.of(inputStream);
        }
        var isSupporter = supporterCheck.isSupporter(slashCommandEvent.getJDA()
                .getShardManager(), slashCommandEvent.getUser());
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
            inputStream = biggifyImage(image, imageFileType, isSupporter);
        } catch (IOException e) {
            log.error("Failed to enlarge image for custom emoji {}, falling back to use original image",
                    customEmoji.getFormatted(), e);
            if (image != null) {
                image.flush();
            }
        }
        return Optional.of(inputStream);
    }

    private InputStream biggifyImage(BufferedImage image, String imageFileType, boolean isSupporter)
            throws IOException {
        var lowerBound = (int) (Math.max(image.getHeight(), image.getWidth()) * getLowerMultiplier(isSupporter));
        var upperBound = Math.max(image.getHeight(), image.getWidth()) * getUpperMultiplier(isSupporter);
        var randomNewSize = random.nextInt(lowerBound, upperBound);
        log.trace("image original {}x{}, lower {} upper {} actual {}", image.getWidth(), image.getHeight(),
                lowerBound, upperBound, randomNewSize);
        var beegImage = Scalr.resize(image, randomNewSize);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(beegImage, imageFileType.substring(1), os);
        image.flush();
        return new ByteArrayInputStream(os.toByteArray());
    }

    private double getLowerMultiplier(boolean isSupporter) {
        return isSupporter ? 2.4 : 1.2;
    }

    private int getUpperMultiplier(boolean isSupporter) {
        return isSupporter ? 4 : 2;
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

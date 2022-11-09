package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.slash.SlashSubCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import dev.nincodedo.ninbot.components.users.UserCommandName.Subcommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserCommand implements SlashSubCommand<Subcommand> {

    private UserService userService;

    public UserCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor, @NotNull Subcommand subcommand) {
        if (subcommand == Subcommand.BIRTHDAY) {
            updateBirthday(event);
            messageExecutor.addEphemeralMessage(Emojis.THUMBS_UP);
        } else if (subcommand == Subcommand.ANNOUNCEMENT) {
            toggleAnnouncement(event.getUser().getId());
            messageExecutor.addEphemeralMessage(Emojis.THUMBS_UP);
        }
        return messageExecutor;
    }

    private void toggleAnnouncement(String userId) {
        userService.toggleBirthdayAnnouncement(userId);
    }

    private void updateBirthday(SlashCommandInteractionEvent event) {
        var birthday = event.getOption(UserCommandName.Option.MONTH.get(), OptionMapping::getAsString) + "-"
                + event.getOption(UserCommandName.Option.DAY.get(), OptionMapping::getAsString);
        var userId = event.getMember().getId();
        var guildId = event.getGuild().getId();
        userService.updateBirthday(userId, guildId, birthday);
    }

    @Override
    public String getName() {
        return UserCommandName.USER.get();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(
                new SubcommandData(Subcommand.BIRTHDAY.get(), "Set your birthday for announcements.")
                        .addOptions(new OptionData(OptionType.INTEGER, UserCommandName.Option.MONTH.get(), "Month of "
                                + "your birthday.",
                                true, true).setMinValue(1).setMaxValue(12))
                        .addOptions(new OptionData(OptionType.INTEGER, UserCommandName.Option.DAY.get(), "Day of your"
                                + " birthday.", true, true).setMinValue(1)
                                .setMaxValue(31)),
                new SubcommandData(Subcommand.ANNOUNCEMENT.get(), "Toggles your birthday announcement"
                        + " on or off."));
    }

    @Override
    public Class<Subcommand> enumSubcommandClass() {
        return Subcommand.class;
    }
}

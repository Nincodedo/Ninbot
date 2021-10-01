package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserCommand implements SlashCommand {

    private UserService userService;

    public UserCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(SlashCommandEvent slashCommandEvent) {
        var messageExecutor = new SlashCommandEventMessageExecutor(slashCommandEvent);
        if (slashCommandEvent.getSubcommandName() == null) {
            return messageExecutor;
        }
        switch (UserCommandName.Subcommand.valueOf(slashCommandEvent.getSubcommandName().toUpperCase())) {
            case BIRTHDAY -> {
                updateBirthday(slashCommandEvent);
                messageExecutor.addEphemeralMessage(Emojis.THUMBS_UP);
            }
            case ANNOUNCEMENT -> {
                toggleAnnouncement(slashCommandEvent);
                messageExecutor.addEphemeralMessage(Emojis.THUMBS_UP);
            }
        }
        return messageExecutor;
    }

    private void toggleAnnouncement(SlashCommandEvent slashCommandEvent) {
        var userId = slashCommandEvent.getUser().getId();
        userService.toggleBirthdayAnnouncement(userId);

    }

    private void updateBirthday(SlashCommandEvent slashCommandEvent) {
        var birthday = slashCommandEvent.getOption(UserCommandName.Option.MONTH.get()).getAsString() + "-"
                + slashCommandEvent.getOption(UserCommandName.Option.DAY.get())
                .getAsString();
        var userId = slashCommandEvent.getMember().getId();
        var guildId = slashCommandEvent.getGuild().getId();
        userService.updateBirthday(userId, guildId, birthday);
    }

    @Override
    public String getName() {
        return UserCommandName.USER.get();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(
                new SubcommandData(UserCommandName.Subcommand.BIRTHDAY.get(), "Set your birthday for announcements.")
                        .addOption(OptionType.INTEGER, UserCommandName.Option.MONTH.get(), "Month of your birthday.",
                                true)
                        .addOption(OptionType.INTEGER, UserCommandName.Option.DAY.get(), "Day of your birthday.", true),
                new SubcommandData(UserCommandName.Subcommand.ANNOUNCEMENT.get(), "Toggles your birthday announcement"
                        + " on or off."));
    }
}

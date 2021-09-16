package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UserCommand implements SlashCommand {

    private UserRepository userRepository;

    public UserCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(SlashCommandEvent slashCommandEvent) {
        if (slashCommandEvent.getSubcommandName() == null) {
            return;
        }
        switch (UserCommandName.Subcommand.valueOf(slashCommandEvent.getSubcommandName())) {
            case BIRTHDAY -> {
                updateBirthday(slashCommandEvent);
                slashCommandEvent.reply(Emojis.THUMBS_UP).setEphemeral(true).queue();
            }
            case ANNOUNCEMENT -> toggleAnnouncement(slashCommandEvent);
        }
    }

    private void toggleAnnouncement(SlashCommandEvent slashCommandEvent) {
        userRepository.getFirstByUserId(slashCommandEvent.getUser().getId())
                .ifPresent(ninbotUser -> ninbotUser.setAnnounceBirthday(!ninbotUser.getAnnounceBirthday()));
        slashCommandEvent.reply(Emojis.THUMBS_UP).setEphemeral(true).queue();
    }

    private void updateBirthday(SlashCommandEvent slashCommandEvent) {
        var birthday = slashCommandEvent.getOption(UserCommandName.Option.MONTH.get()).getAsString() + "-"
                + slashCommandEvent.getOption(UserCommandName.Option.DAY.get())
                .getAsString();
        String userId = slashCommandEvent.getMember().getId();
        var optionalUser = userRepository.getFirstByUserId(userId);
        NinbotUser ninbotUser;
        if (optionalUser.isPresent()) {
            ninbotUser = optionalUser.get();
        } else {
            ninbotUser = new NinbotUser();
            ninbotUser.setUserId(userId);
            ninbotUser.setServerId(slashCommandEvent.getGuild().getId());
        }
        ninbotUser.setBirthday(birthday);
        userRepository.save(ninbotUser);
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

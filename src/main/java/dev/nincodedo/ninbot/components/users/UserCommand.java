package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.common.Emojis;
import dev.nincodedo.ninbot.common.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
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
        switch (slashCommandEvent.getSubcommandName()) {
            case "birthday" -> {
                updateBirthday(slashCommandEvent);
                slashCommandEvent.reply(Emojis.THUMBS_UP).setEphemeral(true).queue();
            }
            case "announcement" -> toggleAnnouncement(slashCommandEvent);
            default -> {
            }
        }
    }

    private void toggleAnnouncement(SlashCommandEvent slashCommandEvent) {
        //TODO add toggle for birthday announcements
    }

    private void updateBirthday(SlashCommandEvent slashCommandEvent) {
        var birthday = slashCommandEvent.getOption("month").getAsString() + "-" + slashCommandEvent.getOption("day")
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
        return "user";
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Collections.emptyList();
    }

    @Override
    public List<SubcommandData> getSubcommandDatas() {
        return Arrays.asList(
                new SubcommandData("birthday", "Set your birthday for announcements.")
                        .addOption(OptionType.INTEGER, "month", "Month of your birthday.", true)
                        .addOption(OptionType.INTEGER, "day", "Day of your birthday.", true),
                new SubcommandData("announcement", "Toggles your birthday announcement on or off."));
    }
}

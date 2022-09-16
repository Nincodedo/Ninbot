package dev.nincodedo.ninbot.common.command.slash;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public interface SlashCommand extends Command<SlashCommandEventMessageExecutor, SlashCommandInteractionEvent> {

    default String getDescription() {
        return getDescription(defaultLocale);
    }

    default String getDescription(Locale locale) {
        return resourceBundle(locale).getString("command." + getName() + ".description");
    }

    default ResourceBundle resourceBundle() {
        return resourceBundle(defaultLocale);
    }

    default ResourceBundle resourceBundle(Locale locale) {
        return ResourceBundle.getBundle("lang", locale);
    }

    default ResourceBundle resourceBundle(DiscordLocale discordLocale) {
        return ResourceBundle.getBundle("lang", Locale.forLanguageTag(discordLocale.getLocale()));
    }

    default String resource(String resourceBundleKey) {
        return resourceBundle().getString(resourceBundleKey);
    }

    default List<OptionData> getCommandOptions() {
        return Collections.emptyList();
    }

    default List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    default MessageExecutor<SlashCommandEventMessageExecutor> execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        return executeCommandAction(slashCommandEvent);
    }

    MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(@NotNull SlashCommandInteractionEvent slashCommandEvent);
}

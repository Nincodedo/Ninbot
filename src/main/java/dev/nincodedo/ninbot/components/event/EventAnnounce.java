package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ResourceBundle;
import java.util.TimerTask;

@Slf4j
class EventAnnounce extends TimerTask {

    private Event event;
    private Guild guild;
    private int minutesBeforeStart;
    private ConfigService configService;

    EventAnnounce(Event event, int minutesBeforeStart, ConfigService configService, Guild guild) {
        this.event = event;
        this.minutesBeforeStart = minutesBeforeStart;
        this.configService = configService;
        this.guild = guild;
    }

    @Override
    public void run() {
        log.debug("Running announce for event {}", event.getId());
        var serverId = event.getServerId();
        var config = configService.getSingleValueByName(serverId, ConfigConstants.ANNOUNCE_CHANNEL);
        config.ifPresent(announceChannelId -> {
            var channel = guild.getTextChannelById(announceChannelId);
            var gameRoleId = guild.getRolesByName(event.getGameName(), true).get(0);
            event.setResourceBundle(ResourceBundle.getBundle("lang", LocaleService.getLocale(guild)));
            channel.sendMessage(event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart)).queue();
        });
    }
}

package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ResourceBundle;
import java.util.TimerTask;

@Log4j2
class EventAnnounce extends TimerTask {

    private Event event;
    private Guild guild;
    private int minutesBeforeStart;
    private ConfigService configService;
    private LocaleService localeService;

    EventAnnounce(Event event, int minutesBeforeStart, ConfigService configService, Guild guild,
            LocaleService localeService) {
        this.event = event;
        this.minutesBeforeStart = minutesBeforeStart;
        this.configService = configService;
        this.guild = guild;
        this.localeService = localeService;
    }

    @Override
    public void run() {
        log.debug("Running announce for event {}", event.getId());
        val serverId = event.getServerId();
        val config = configService.getSingleValueByName(serverId, ConfigConstants.ANNOUNCE_CHANNEL);
        config.ifPresent(announceChannelId -> {
            val channel = guild.getTextChannelById(announceChannelId);
            val gameRoleId = guild.getRolesByName(event.getGameName(), true).get(0);
            event.setResourceBundle(ResourceBundle.getBundle("lang", localeService.getLocale(guild)));
            channel.sendMessage(event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart)).queue();
        });
    }
}

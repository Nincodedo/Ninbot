package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ResourceBundle;
import java.util.TimerTask;

class EventAnnounce extends TimerTask {

    private static final org.apache.logging.log4j.Logger log =
            org.apache.logging.log4j.LogManager.getLogger(EventAnnounce.class);
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
        String serverId = event.getServerId();
        final java.util.Optional<java.lang.String> config = configService.getSingleValueByName(serverId,
                ConfigConstants.ANNOUNCE_CHANNEL);
        config.ifPresent(announceChannelId -> {
            final net.dv8tion.jda.api.entities.TextChannel channel = guild.getTextChannelById(announceChannelId);
            final net.dv8tion.jda.api.entities.Role gameRoleId = guild.getRolesByName(event.getGameName(), true).get(0);
            event.setResourceBundle(ResourceBundle.getBundle("lang", LocaleService.getLocale(guild)));
            channel.sendMessage(event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart)).queue();
        });
    }
}

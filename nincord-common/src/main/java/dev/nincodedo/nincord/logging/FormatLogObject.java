package dev.nincodedo.nincord.logging;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@UtilityClass
public class FormatLogObject {

    public static final String NAME_ID_ENTITY_FORMAT = "%s(%s)";

    /**
     * Returns the info for the event with the guild, channel, and author's info.
     *
     * @param event JDA event
     * @return String of guild name(guild id), channel name(channel id), username(user id)
     */
    public static String eventInfo(MessageReceivedEvent event) {
        return STR."Server: \{guildName(event.getGuild())}, Channel: \{channelInfo(event.getChannel())}, Author: \{userInfo(event.getAuthor())}";
    }

    /**
     * Returns the guild name and id formatted for logging.
     *
     * @param guild JDA guild
     * @return String of guild name(guild id)
     */
    public static String guildName(Guild guild) {
        if (guild == null) {
            return "null guild";
        }
        return NAME_ID_ENTITY_FORMAT.formatted(guild.getName(), guild.getId());
    }

    /**
     * Returns the guild name, id and owner name, id formatted for logging.
     *
     * @param guild JDA guild
     * @return String of guild name(guild id), owner name(owner id)
     */
    public static String guildInfo(Guild guild) {
        if (guild == null) {
            return "null guild";
        }
        return "Server %s, Owner %s".formatted(guildName(guild), memberInfo(guild.retrieveOwner().complete()));
    }

    /**
     * Returns the member name and id formatted for logging.
     *
     * @param member JDA member
     * @return String of member name(member id)
     */
    public static String memberInfo(Member member) {
        return NAME_ID_ENTITY_FORMAT.formatted(member.getEffectiveName(), member.getId());
    }

    /**
     * Returns the channel name and id formatted for logging.
     *
     * @param channel JDA channel
     * @return String of channel name(channel id)
     */
    public static String channelInfo(Channel channel) {
        if (channel == null) {
            return "null channel";
        }
        return NAME_ID_ENTITY_FORMAT.formatted(channel.getName(), channel.getId());
    }

    /**
     * Returns the username and id formatted for logging.
     *
     * @param user JDA user
     * @return String of username(user id)
     */
    public static String userInfo(User user) {
        if (user == null) {
            return "null user";
        }
        return NAME_ID_ENTITY_FORMAT.formatted(user.getName(), user.getId());
    }

    /**
     * Returns the role and id formatted for logging.
     *
     * @param role JDA role
     * @return String of role(role id)
     */
    public static String roleInfo(Role role) {
        if (role == null) {
            return "null role";
        }
        return NAME_ID_ENTITY_FORMAT.formatted(role.getName(), role.getId());
    }
}

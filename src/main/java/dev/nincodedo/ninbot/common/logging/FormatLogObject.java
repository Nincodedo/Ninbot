package dev.nincodedo.ninbot.common.logging;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

@UtilityClass
public class FormatLogObject {

    public static final String NAME_ID_ENTITY_FORMAT = "%s(%s)";

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
        return String.format(NAME_ID_ENTITY_FORMAT, guild.getName(), guild.getId());
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
        return String.format("Server %s, Owner %s", guildName(guild), memberInfo(guild.retrieveOwner()
                .complete()));
    }

    /**
     * Returns the member name and id formatted for logging.
     *
     * @param member JDA member
     * @return String of member name(member id)
     */
    public static String memberInfo(Member member) {
        return String.format(NAME_ID_ENTITY_FORMAT, member.getEffectiveName(), member.getId());
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
        return String.format(NAME_ID_ENTITY_FORMAT, channel.getName(), channel.getId());
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
        return String.format(NAME_ID_ENTITY_FORMAT, user.getName(), user.getId());
    }

    public static String roleInfo(Role role) {
        if (role == null) {
            return "null role";
        }
        return String.format(NAME_ID_ENTITY_FORMAT, role.getName(), role.getId());
    }
}

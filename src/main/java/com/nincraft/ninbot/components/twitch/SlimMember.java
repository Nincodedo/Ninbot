package com.nincraft.ninbot.components.twitch;

import lombok.Data;

@Data
class SlimMember {
    String userId;
    String guildId;

    SlimMember(String userId, String guildId) {
        this.userId = userId;
        this.guildId = guildId;
    }
}

package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.api.JDA;

public interface Schedulable {
    void scheduleAll(JDA jda);
}

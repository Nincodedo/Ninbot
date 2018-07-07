package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.core.JDA;

public abstract class AbstractScheduler<T> {
    public abstract void scheduleAll(JDA jda);

    public void scheduleOne(T event, JDA jda) {

    }
}

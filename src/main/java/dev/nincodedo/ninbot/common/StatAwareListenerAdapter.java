package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StatAwareListenerAdapter extends ListenerAdapter {

    private StatManager statManager;

    public StatAwareListenerAdapter(StatManager statManager) {
        this.statManager = statManager;
    }

    public void countOneStat(String name, String serverId) {
        statManager.addOneCount(name, StatCategory.LISTENER, serverId);
    }
}

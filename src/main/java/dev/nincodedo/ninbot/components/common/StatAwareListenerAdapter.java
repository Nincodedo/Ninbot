package dev.nincodedo.ninbot.components.common;

import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public class StatAwareListenerAdapter extends ListenerAdapter {

    @Autowired
    private StatManager statManager;

    public void countOneStat(String name, String serverId) {
        statManager.addOneCount(name, StatCategory.LISTENER, serverId);
    }
}

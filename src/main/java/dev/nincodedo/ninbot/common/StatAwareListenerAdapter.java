package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatAwareListenerAdapter extends ListenerAdapter {

    private StatManager statManager;
    private ExecutorService executorService;

    public StatAwareListenerAdapter(StatManager statManager) {
        this.statManager = statManager;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("stat-counter"));
    }

    public void countOneStat(String name, String serverId) {
        executorService.execute(() -> statManager.addOneCount(name, StatCategory.LISTENER, serverId));
    }
}

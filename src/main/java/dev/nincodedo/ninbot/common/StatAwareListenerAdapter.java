package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;
import io.micrometer.core.instrument.util.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatAwareListenerAdapter extends BaseListenerAdapter {

    private StatManager statManager;
    private ExecutorService executorService;

    public StatAwareListenerAdapter(ServerLoggerFactory serverLoggerFactory, StatManager statManager) {
        super(serverLoggerFactory);
        this.statManager = statManager;
        this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory("stat-counter"));
    }

    public void countOneStat(String name, String guildId) {
        executorService.execute(() -> statManager.addOneCount(name, StatCategory.LISTENER, guildId));
    }
}

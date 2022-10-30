package dev.nincodedo.ninbot.common;

import dev.nincodedo.ninbot.components.stats.StatCategory;
import dev.nincodedo.ninbot.components.stats.StatManager;

import java.util.concurrent.ExecutorService;

public abstract class StatAwareListenerAdapter extends BaseListenerAdapter {

    private StatManager statManager;
    private ExecutorService executorService;

    protected StatAwareListenerAdapter(StatManager statManager, ExecutorService executorService) {
        this.statManager = statManager;
        this.executorService = executorService;
    }

    protected void countOneStat(String name, String guildId) {
        executorService.execute(() -> statManager.addOneCount(name, StatCategory.LISTENER, guildId));
    }
}

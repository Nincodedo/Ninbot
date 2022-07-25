package dev.nincodedo.ninbot.common;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Schedulable<T, S extends Scheduler<T, ?>> {

    ExecutorService executorService = Executors.newCachedThreadPool(new NamedThreadFactory("scheduler"));

    default void scheduleAll(ShardManager shardManager) {
        getScheduler().findAllOpenItems()
                .forEach(schedulable -> executorService.execute(() -> scheduleOne(schedulable, shardManager)));
    }

    void scheduleOne(T schedulable, ShardManager shardManager);

    default void addOne(T schedulable, ShardManager shardManager) {
        getScheduler().save(schedulable);
        scheduleOne(schedulable, shardManager);
    }

    S getScheduler();
}

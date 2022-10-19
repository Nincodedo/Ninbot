package dev.nincodedo.ninbot.common;

import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Timer;
import java.util.concurrent.ExecutorService;

public abstract class Schedulable<T, S extends Scheduler<T, ?>> {

    protected ExecutorService executorService;
    protected String schedulerName;
    protected Timer timer;


    protected Schedulable(ExecutorService schedulerExecutorService) {
        this.executorService = schedulerExecutorService;
        this.schedulerName = getSchedulerName();
        this.timer = new Timer(schedulerName, true);
    }

    protected abstract String getSchedulerName();

    protected Timer getTimer() {
        return timer;
    }

    protected void scheduleAll(ShardManager shardManager) {
        getScheduler().findAllOpenItems()
                .forEach(schedulable -> executorService.execute(() -> scheduleOne(schedulable, shardManager)));
    }

    protected abstract void scheduleOne(T schedulable, ShardManager shardManager);

    protected void addOne(T schedulable, ShardManager shardManager) {
        getScheduler().save(schedulable);
        scheduleOne(schedulable, shardManager);
    }

    protected abstract S getScheduler();
}

package com.nincraft.ninbot.components.ac;

import com.nincraft.ninbot.components.common.Schedulable;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Component
public class TurnipPricesScheduler implements Schedulable {

    private TurnipPricesManager turnipPricesManager;
    private VillagerRepository villagerRepository;
    private TurnipPricesRepository turnipPricesRepository;

    public TurnipPricesScheduler(TurnipPricesManager turnipPricesManager, VillagerRepository villagerRepository,
            TurnipPricesRepository turnipPricesRepository) {
        this.turnipPricesManager = turnipPricesManager;
        this.villagerRepository = villagerRepository;
        this.turnipPricesRepository = turnipPricesRepository;
    }


    @Override
    public void scheduleAll(ShardManager shardManager) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        Date nextSunday = calendar.getTime();
        new Timer().scheduleAtFixedRate(new TurnipTasks(), nextSunday, TimeUnit.DAYS.toMillis(7));
    }


    private class TurnipTasks extends TimerTask {
        @Override
        public void run() {
            TurnipPrices turnipPrices = turnipPricesRepository.findAll().get(0);
            if (turnipPrices.getCreated().getDayOfMonth() != LocalDate.now().getDayOfMonth()) {
                villagerRepository.findAll().forEach(villager -> {
                    villager.setTurnipsOwned(0);
                    villagerRepository.save(villager);
                });
                turnipPricesManager.generateNewWeek();
            }
        }
    }
}

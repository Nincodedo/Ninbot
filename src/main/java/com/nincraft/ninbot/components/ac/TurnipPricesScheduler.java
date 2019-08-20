package com.nincraft.ninbot.components.ac;

import com.nincraft.ninbot.components.common.Schedulable;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Log4j2
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
        log.trace("Scheduling turnips price reset for {}", nextSunday);
        new Timer().scheduleAtFixedRate(new TurnipTasks(), nextSunday, TimeUnit.DAYS.toMillis(7));
    }


    private class TurnipTasks extends TimerTask {
        @Override
        public void run() {
            log.trace("Running turnips price reset");
            TurnipPrices turnipPrices = turnipPricesRepository.findAll().get(0);
            if (turnipPrices.getCreated().getDayOfMonth() != LocalDate.now().getDayOfMonth()) {
                val villagerList = villagerRepository.findAll();
                log.trace("Resetting turnip counts for {} villagers", villagerList.size());
                villagerList.forEach(villager -> {
                    villager.setTurnipsOwned(0);
                    villagerRepository.save(villager);
                });
                turnipPricesManager.generateNewWeek();
            }
        }
    }
}

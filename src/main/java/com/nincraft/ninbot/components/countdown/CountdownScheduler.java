package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.common.AbstractScheduler;
import net.dv8tion.jda.core.JDA;
import org.springframework.stereotype.Component;

@Component
public class CountdownScheduler extends AbstractScheduler<Countdown> {

    private CountdownDao countdownDao;

    public CountdownScheduler(CountdownDao countdownDao) {
        this.countdownDao = countdownDao;
    }

    public void scheduleAll(JDA jda) {
        countdownDao.getAllObjects().forEach(countdown -> scheduleOne(countdown, jda));
    }

    public void scheduleOne(Countdown countdown, JDA jda) {

    }
}

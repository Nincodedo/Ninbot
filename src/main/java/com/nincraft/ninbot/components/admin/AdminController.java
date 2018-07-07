package com.nincraft.ninbot.components.admin;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@Controller
public class AdminController {
    @GetMapping("/admin/shutdown")
    @ResponseBody
    public boolean shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("Couldn't shutdown", e);
            }
            System.exit(0);
        }).start();
        return true;
    }
}

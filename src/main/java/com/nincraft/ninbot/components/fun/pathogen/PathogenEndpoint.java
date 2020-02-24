package com.nincraft.ninbot.components.fun.pathogen;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Log4j2
@Controller
@RequestMapping("/pathogen")
public class PathogenEndpoint {

    private PathogenManager pathogenManager;

    public PathogenEndpoint(PathogenManager pathogenManager) {
        this.pathogenManager = pathogenManager;
    }

    @GetMapping("/wordlist")
    @ResponseBody
    public Set<String> getWordList() {
        return pathogenManager.getWordList();
    }

    @GetMapping("/wordlist/{date}")
    @ResponseBody
    public Set<String> getWordList(@PathVariable String date) {
        return pathogenManager.getWordList(date);
    }
}

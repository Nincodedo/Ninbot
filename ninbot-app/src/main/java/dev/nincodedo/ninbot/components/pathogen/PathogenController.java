package dev.nincodedo.ninbot.components.pathogen;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/v1/pathogen", produces = MediaType.APPLICATION_JSON_VALUE)
public class PathogenController {

    private PathogenManager pathogenManager;

    public PathogenController(PathogenManager pathogenManager) {
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

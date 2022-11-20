package dev.nincodedo.ninbot.components.define;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "define-command-word-source", url = "https://api.urbandictionary.com/v0")
public interface FeignUrbanDictionary {
    @GetMapping(value = "/define")
    UrbanDictionaryResponse defineWord(@RequestParam String term);
}

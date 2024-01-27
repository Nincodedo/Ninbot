package dev.nincodedo.ninbot.components.hugemoji;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ninbot-utils-emojis", url = "https://ninbot-utils.nincodedo.dev/v1/emojis", configuration =
        EmojisFeignConfiguration.class)
public interface EmojisFeign {

    @GetMapping
    feign.Response getEmoji(@RequestParam String emoji);
}

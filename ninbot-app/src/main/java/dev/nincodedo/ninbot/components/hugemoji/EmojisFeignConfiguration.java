package dev.nincodedo.ninbot.components.hugemoji;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class EmojisFeignConfiguration {

    @Bean
    public RequestInterceptor addAccess(@Value("${nincodedo.utils.access.id}") String accessId,
            @Value("${nincodedo.utils.access.secret}") String accessSecret) {
        return template -> template.header("CF-Access-Client-Id", accessId)
                .header("CF-Access-Client-Secret", accessSecret)
                .header("User-Agent", "Ninbot (https://github.com/Nincodedo/Ninbot)");
    }
}

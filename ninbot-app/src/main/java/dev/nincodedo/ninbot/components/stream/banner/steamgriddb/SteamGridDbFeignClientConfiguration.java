package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SteamGridDbFeignClientConfiguration {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Bean
    public RequestInterceptor bearerTokenRequestInterceptor(@Value("${nincodedo.steamgriddbapikey}") String apiKey) {
        return template -> template.header(AUTHORIZATION_HEADER, "%s %s".formatted(TOKEN_TYPE, apiKey))
                .header("User-Agent", "Ninbot (https://github.com/Nincodedo/Ninbot)");
    }
}

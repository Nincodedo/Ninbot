package dev.nincodedo.ninbot.components.stream.banner.steamgriddb;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SteamGridDbFeignClientConfiguration {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";
    @Value("${nincodedo.steamgriddbapikey}")
    private String apiKey;

    @Bean
    public RequestInterceptor bearerTokenRequestInterceptor() {
        return template -> template.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, apiKey));
    }
}

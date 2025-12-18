package ru.practicum.ewm.main.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class WebClientConfig {

    AppProperties appProperties;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(appProperties.getStatsServerUrl()).build();
    }
}

package ru.practicum.ewm.main.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.*;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class StatsClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient(RestTemplate restTemplate, AppProperties appProperties) {
        return new RestStatsClient(
                restTemplate,
                appProperties.getStatsServerUrl(),
                appProperties.getName()
        );
    }
}

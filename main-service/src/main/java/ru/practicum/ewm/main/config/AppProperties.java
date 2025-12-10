package ru.practicum.ewm.main.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name = "main-service";

    private String statsServerUrl = "http://localhost:9090";

    private String timeFormat = "yyyy-MM-dd HH:mm:ss";

    public String getName() {
        return name;
    }

    public String getStatsServerUrl() {
        return statsServerUrl;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatsServerUrl(String statsServerUrl) {
        this.statsServerUrl = statsServerUrl;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
}

package ru.practicum.ewm.main.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.*;

import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeConfig {

    @Bean
    public DateTimeFormatter apiDateTimeFormatter(AppProperties props) {
        return DateTimeFormatter.ofPattern(props.getTimeFormat());
    }

    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer(DateTimeFormatter apiDateTimeFormatter) {
        return new LocalDateTimeSerializer(apiDateTimeFormatter);
    }
}

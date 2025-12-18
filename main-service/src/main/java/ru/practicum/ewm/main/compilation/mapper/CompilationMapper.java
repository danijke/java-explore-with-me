package ru.practicum.ewm.main.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.category.mapper.CategoryMapper;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.mapper.EventMapper;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.mapper.UserMapper;

import java.time.format.DateTimeFormatter;
import java.util.*;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toDto(Compilation entity,
                                       Map<Long, Long> confirmedByEventId,
                                       Map<Long, Long> viewsByEventId,
                                       DateTimeFormatter apiDateTimeFormatter) {

        List<EventShortDto> shortDtos = new ArrayList<>();
        for (Event e : entity.getEvents()) {
            EventShortDto es = EventMapper.toShortDto(
                    e,
                    CategoryMapper.toDto(e.getCategory()),
                    UserMapper.toShortDto(e.getInitiator()),
                    apiDateTimeFormatter,
                    confirmedByEventId.get(e.getId()),
                    viewsByEventId.get(e.getId())
            );
            shortDtos.add(es);
        }

        return CompilationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pinned(Boolean.TRUE.equals(entity.getPinned()))
                .events(shortDtos)
                .build();
    }
}

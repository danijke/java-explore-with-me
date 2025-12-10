package ru.practicum.ewm.main.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class ParticipationRequestMapper {

    public static ParticipationRequestDto toDto(ParticipationRequest entity, DateTimeFormatter isoFormatter) {
        return ParticipationRequestDto.builder()
                .id(entity.getId())
                .created(entity.getCreated() == null ? null : entity.getCreated().format(isoFormatter))
                .event(entity.getEvent().getId())
                .requester(entity.getRequester().getId())
                .status(entity.getStatus().name())
                .build();
    }
}

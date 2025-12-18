package ru.practicum.ewm.main.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.user.dto.*;
import ru.practicum.ewm.main.user.model.User;

@UtilityClass
public class UserMapper {

    public static User toEntity(NewUserRequest dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(User entity) {
        return UserShortDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}

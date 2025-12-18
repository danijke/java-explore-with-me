package ru.practicum.ewm.main.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.user.dto.*;

import java.util.List;

public interface UserService {

    List<UserDto> findAll(List<Long> ids, Pageable pageable);

    UserDto create(NewUserRequest dto);

    void delete(Long userId);
}

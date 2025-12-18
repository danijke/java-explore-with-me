package ru.practicum.ewm.main.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.dto.*;
import ru.practicum.ewm.main.user.mapper.UserMapper;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;
import ru.practicum.ewm.main.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll(List<Long> ids, Pageable pageable) {
        List<User> entities;
        if (ids != null && !ids.isEmpty()) {
            entities = userRepository.findAllByIdIn(ids);
        } else {
            entities = userRepository.findAllBy(pageable);
        }
        return entities.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest dto) {
        User toSave = UserMapper.toEntity(dto);
        User saved = userRepository.save(toSave);
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException();
        }
    }
}

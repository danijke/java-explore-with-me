package ru.practicum.ewm.main.category.service;

import ru.practicum.ewm.main.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(int from, int size) ;

    CategoryDto getById(Long catId) ;

    CategoryDto create(CategoryDto dto) ;

    void delete(Long catId) ;

    CategoryDto update(Long catId, CategoryDto dto) ;
}

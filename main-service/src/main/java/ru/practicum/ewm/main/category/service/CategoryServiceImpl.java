package ru.practicum.ewm.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.mapper.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.exception.*;
import ru.practicum.ewm.main.util.PageUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository repo;

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageUtils.offset(from, size);
        return repo.findAll(pageable).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long catId) {
        return repo.findById(catId)
                .map(CategoryMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto dto) {
        if (repo.existsByName(dto.getName())) {
            throw new ConflictException("category name must be unique");
        }
        Category saved = repo.save(CategoryMapper.toCategory(dto));
        return CategoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        if (!repo.existsById(catId)) {
            throw new NotFoundException();
        }
        repo.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto dto) {
        Category category = repo.findById(catId)
                .orElseThrow(NotFoundException::new);
        category.setName(dto.getName());
        Category updated = repo.save(category);
        return CategoryMapper.toDto(updated);
    }
}

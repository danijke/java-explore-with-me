package ru.practicum.ewm.main.utils;

import org.springframework.data.domain.Pageable;

public class PageUtils {

    public static Pageable offset(int from, int size) {
        return new OffsetBasedPageRequest(from, size);
    }
}

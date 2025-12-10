package ru.practicum.ewm.main.util;

import org.springframework.data.domain.Pageable;

public class PageUtils {

    public static Pageable offset(int from, int size) {
        return new OffsetBasedPageRequest(from, size);
    }
}

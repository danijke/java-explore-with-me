package ru.practicum.ewm.main.util;

import org.springframework.data.domain.*;

import java.io.*;

public class OffsetBasedPageRequest implements Pageable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int offset;
    private final int pageSize;
    private final Sort sort;

    public OffsetBasedPageRequest(int from, int size) {
        this(from, size, Sort.unsorted());
    }

    public OffsetBasedPageRequest(int from, int size, Sort sort) {
        if (from < 0) {
            throw new IllegalArgumentException("from must be >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
        this.offset = from;
        this.pageSize = size;
        this.sort = sort == null ? Sort.unsorted() : sort;
    }

    @Override
    public int getPageNumber() {
        return pageSize == 0 ? 0 : offset / pageSize;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        int newOffset = (int) (getOffset() - getPageSize());
        if (newOffset < 0) {
            newOffset = 0;
        }
        return new OffsetBasedPageRequest(newOffset, getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber must be >= 0");
        }
        return new OffsetBasedPageRequest(pageNumber * getPageSize(), getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }
}

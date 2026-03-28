package com.realestate.rental.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated list wrapper (AGENT.md — never return a raw JSON array).
 */
public record ApiPageResponse<T>(
        List<T> data,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> ApiPageResponse<T> from(Page<T> page) {
        return new ApiPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

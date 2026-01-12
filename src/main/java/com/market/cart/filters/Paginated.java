package com.market.cart.filters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic pagination wrapper used to expose Spring Data {@link Page} results
 * in a simplified, API-friendly structure.
 *
 * <p>
 * Encapsulates page metadata along with the actual page content,
 * decoupling API responses from Spring Data internals.
 * </p>
 *
 * @param <T> the type of elements contained in the page
 */
@Getter
@Setter
@Builder
public class Paginated<T> {

    List<T> data;
    long totalElements;
    int totalPages;
    int numberOfElements;
    int currentPage;
    int pageSize;

    /**
     * Creates a {@link Paginated} wrapper from a Spring Data {@link Page}.
     *
     * @param page the Spring Data page instance
     * @param <T> the type of elements in the page
     * @return a populated {@link Paginated} instance
     */
    public static <T> Paginated<T> onPage(Page<T> page) {

        return Paginated.<T> builder()
                .data(page.getContent())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .build();
    }

}

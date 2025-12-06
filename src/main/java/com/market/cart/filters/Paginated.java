package com.market.cart.filters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

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

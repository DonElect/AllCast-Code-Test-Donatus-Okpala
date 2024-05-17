package com.donatus.simpletaskmanager.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> content;
    private int pageNum;
    private int pageSize;
    private long totalElement;
    //private int totalPages;
    private boolean isLast;
}

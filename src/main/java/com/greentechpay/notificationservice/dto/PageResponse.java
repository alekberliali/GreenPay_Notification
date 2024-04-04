package com.greentechpay.notificationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PageResponse<T> {
    private Long totalElements;
    private Integer totalPages;
    private List<T> content;
}

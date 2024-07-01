package com.greentechpay.notificationservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Body {
    private BigDecimal amount;
    private String currency;
    private String requestField;
    private LocalDateTime date;
    private String description;
}

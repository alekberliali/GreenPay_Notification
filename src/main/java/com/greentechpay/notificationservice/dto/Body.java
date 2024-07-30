package com.greentechpay.notificationservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Body {
    private BigDecimal amount;
    private String currency;
    private String date;
    private String description;
}

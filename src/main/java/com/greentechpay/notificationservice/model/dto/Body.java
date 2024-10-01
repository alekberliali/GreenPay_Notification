package com.greentechpay.notificationservice.model.dto;

import com.greentechpay.notificationservice.model.enumarated.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Body {
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String date;
    private String description;
}

package com.greentechpay.notificationservice.model.dto;

import com.greentechpay.notificationservice.model.enumarated.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Body {
    private BigDecimal amount;
    private String currency;
    private Status status;
    private String date;
    private String description;
}

package com.greentechpay.notificationservice.model.enumarated;

import java.util.List;

public enum Status {
    Success,
    TransactionSuccessfully,
    Fail,
    Pending;

    public static List<Status> getStatusList() {
        return List.of(Success, TransactionSuccessfully, Fail, Pending);
    }
}

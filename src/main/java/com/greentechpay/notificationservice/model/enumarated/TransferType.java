package com.greentechpay.notificationservice.model.enumarated;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferType {
    IbanToPhoneNumber(""),
    IbanToIban(""),
    UIdToUId(""),
    UIdToIban(""),
    IbanToUId(""),
    BalanceToCard("balanceToCard"),
    CardToBalance("cardToBalance"),
    BillingPayment("billing"),
    Qr(""),
    Nfc(""),
    NONE("none");

    private final String value;
}

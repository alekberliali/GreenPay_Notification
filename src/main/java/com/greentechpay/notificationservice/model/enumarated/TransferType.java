package com.greentechpay.notificationservice.model.enumarated;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferType {
    IbanToPhoneNumber("ibanToPhoneNumber"),
    IbanToIban("ibanToIban"),
    UIdToUId("uIdToUId"),
    UIdToIban("uIdToIban"),
    IbanToUId("ibanToUid"),
    BalanceToCard("balanceToCard"),
    CardToBalance("cardToBalance"),
    BillingPayment("billingPayment"),
    Qr("qr"),
    Nfc("nfc"),
    NONE("none");

    private final String value;
}
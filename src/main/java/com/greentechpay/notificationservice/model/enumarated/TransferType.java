package com.greentechpay.notificationservice.model.enumarated;

public enum TransferType {
    IbanToPhoneNumber,
    IbanToIban,
    UIdToUId,
    UIdToIban,
    IbanToUId,
    BalanceToCard,
    CardToBalance,
    BillingPayment,
    Qr,
    Nfc,
    NONE
}

package com.scolabs.appiou.models;

import java.util.UUID;

public class PaymentInformation {
    String senderUUID;
    String receiverUUID;
    double amount;
    String timestamp;
    String transactionUUID;

    public PaymentInformation() {

    }

    public String getTransactionUUID() {
        return transactionUUID;
    }

    public PaymentInformation(String senderUUID, String receiverUUID, double amount, String timestamp) {
        this.senderUUID = senderUUID;
        this.receiverUUID = receiverUUID;
        this.amount = amount;
        this.timestamp = timestamp;
        transactionUUID = UUID.randomUUID().toString();
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    public String getReceiverUUID() {
        return receiverUUID;
    }

    public double getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

package com.example.salamtek1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum PaymentStatus { PENDING, LATE, PAID, AVAILABLE }

class Payment {
    private String paymentId, accidentId, payerNationalId, recipientNationalId, paymentMethod;
    private double amount;
    private PaymentStatus status = PaymentStatus.PENDING;
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(30), paidDate;

    public Payment(String paymentId, String accidentId, String payerNationalId,
                   String recipientNationalId, double amount) {
        this.paymentId = paymentId;
        this.accidentId = accidentId;
        this.payerNationalId = payerNationalId;
        this.recipientNationalId = recipientNationalId;
        this.amount = amount;
    }

    public String getPaymentId() { 
        return paymentId;
    }
    public String getPayerNationalId() {
        return payerNationalId;
    }
    public String getRecipientNationalId() {
        return recipientNationalId;
    }
    public double getAmount() {
        return amount;
    }
    public PaymentStatus getStatus() {
        return status;
    }

    public boolean isLate() {
        return LocalDateTime.now().isAfter(dueDate) && status == PaymentStatus.PENDING;
    }
    public double getLateFee() {
        return isLate() ? amount * 0.05 : 0.0;
    }
    public double getTotalAmountDue() {
        return amount + getLateFee();
    }

    public void processPayment(String paymentMethod) {
        this.status = PaymentStatus.PAID;
        this.paidDate = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
    }

    public void markAsAvailable() {
        this.status = PaymentStatus.AVAILABLE;
    }

    public String getFormattedDueDate() {
        return dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

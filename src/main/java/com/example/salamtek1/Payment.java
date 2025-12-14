package com.example.salamtek1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum PaymentStatus { PENDING, LATE, PAID, AVAILABLE }

class Payment implements Persistable, Validatable {
    private String paymentId;
    private String accidentId;
    private String payerNationalId;
    private String recipientNationalId;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime paidDate;
    private String paymentMethod;

    public Payment(String paymentId, String accidentId, String payerNationalId,
                   String recipientNationalId, double amount) {
        this.paymentId = paymentId;
        this.accidentId = accidentId;
        this.payerNationalId = payerNationalId;
        this.recipientNationalId = recipientNationalId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.dueDate = LocalDateTime.now().plusDays(30);
    }

    @Override
    public void validate() throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Payment amount must be positive");
        }
        if (payerNationalId.equals(recipientNationalId)) {
            throw new ValidationException("Payer and recipient must be different");
        }
    }

    public String getPaymentId() { return paymentId; }
    public String getAccidentId() { return accidentId; }
    public String getPayerNationalId() { return payerNationalId; }
    public String getRecipientNationalId() { return recipientNationalId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getPaidDate() { return paidDate; }
    public String getPaymentMethod() { return paymentMethod; }

    public boolean isLate() {
        return LocalDateTime.now().isAfter(dueDate) && status == PaymentStatus.PENDING;
    }

    public double getLateFee() {
        if (isLate()) {
            return amount * 0.05;
        }
        return 0.0;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dueDate.format(formatter);
    }

    @Override
    public String toFileFormat() {
        return String.format("PAYMENT|%s|%s|%s|%s|%.2f|%s",
                paymentId, accidentId, payerNationalId, recipientNationalId, amount, status);
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}

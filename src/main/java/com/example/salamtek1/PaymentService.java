package com.example.salamtek1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PaymentService {
    private DatabaseManager db = DatabaseManager.getInstance();

    public String createPayment(String accidentId, String payerNationalId,String recipientNationalId, double amount) {
        Payment payment = new Payment(null, accidentId, payerNationalId, recipientNationalId, amount);
        String paymentId = db.addPayment(payment);
        payment = db.getPayment(paymentId);
        payment.markAsAvailable();
        db.updatePayment(payment);
        return paymentId;
    }

    public void processPayment(String paymentId, String paymentMethod) {
        Payment payment = db.getPayment(paymentId);
        payment.processPayment(paymentMethod);
        db.updatePayment(payment);
    }

    public Map<String, Object> getPaymentSummary(String nationalId) {
        Map<String, Object> summary = new HashMap<>();
        ArrayList<Payment> paymentsOwed = db.getPaymentsByPayer(nationalId);
        double totalOwed = paymentsOwed.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .mapToDouble(Payment::getTotalAmountDue).sum();

        List<Payment> paymentsReceivable = db.getPaymentsByRecipient(nationalId);
        double totalAvailable = paymentsReceivable.stream()
                .filter(p -> p.getStatus() == PaymentStatus.AVAILABLE)
                .mapToDouble(Payment::getAmount).sum();

        summary.put("totalOwed", totalOwed);
        summary.put("totalAvailable", totalAvailable);
        summary.put("paymentsOwed", paymentsOwed);
        summary.put("paymentsReceivable", paymentsReceivable);
        return summary;
    }

    public void withdrawFunds(String nationalId) {
        List<Payment> availablePayments = db.getPaymentsByRecipient(nationalId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.AVAILABLE)
                .collect(Collectors.toList());

        for (Payment payment : availablePayments) {
            payment.processPayment("Bank Transfer");
            db.updatePayment(payment);
        }
    }
}

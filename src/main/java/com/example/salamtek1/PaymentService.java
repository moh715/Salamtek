package com.example.salamtek1;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.stream.Collectors;

class PaymentService {
    private DatabaseManager db = DatabaseManager.getInstance();

    public String createPayment(String accidentId, String payerNationalId,
                                String recipientNationalId, double amount) throws ValidationException {
        Payment payment = new Payment(null, accidentId, payerNationalId, recipientNationalId, amount);
        String paymentId = db.addPayment(payment);
        // Status remains PENDING - payer needs to pay first
        return paymentId;
    }

    public void processPayment(String paymentId, String paymentMethod) {
        Payment payment = db.getPayment(paymentId);
        payment.processPayment(paymentMethod);
        db.updatePayment(payment);
    }

    public HashMap<String, Object> getPaymentSummary(String nationalId) {
        HashMap<String, Object> summary = new HashMap<>();
        ArrayList<Payment> paymentsOwed = db.getPaymentsByPayer(nationalId);
        double totalOwed = paymentsOwed.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING ||
                        p.getStatus() == PaymentStatus.LATE)
                .mapToDouble(Payment::getTotalAmountDue).sum();

        ArrayList<Payment> paymentsReceivable = db.getPaymentsByRecipient(nationalId);
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
        ArrayList<Payment> availablePayments = db.getPaymentsByRecipient(nationalId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.AVAILABLE)
                .collect(Collectors.toCollection(ArrayList::new));

        for (Payment payment : availablePayments) {
            payment.setStatus(PaymentStatus.PAID);
            db.updatePayment(payment);
        }
    }
}

package com.example.salamtek1;

public class Bill {

    private int billId;
    private double amount;
    private String dueDate;
    private Customer didIt, doneon;

    public Bill(int billId, double amount, String dueDate) {
        this.billId = billId;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public double getAmount() {
        return amount;
    }
}
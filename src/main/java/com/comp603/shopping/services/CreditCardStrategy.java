package com.comp603.shopping.services;

public class CreditCardStrategy implements PaymentStrategy {

    private String name;
    private String cardNumber;
    private String cvv;
    private String dateOfExpiry;

    public CreditCardStrategy(String name, String cardNumber, String cvv, String dateOfExpiry) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.dateOfExpiry = dateOfExpiry;
    }

    @Override
    public boolean pay(double amount) {
        // In a real app, we would validate with a bank API.
        // Here we simulate success if all fields are provided
        if (cardNumber != null && !cardNumber.isEmpty() &&
            cvv != null && !cvv.isEmpty() &&
            dateOfExpiry != null && !dateOfExpiry.isEmpty()) {
            System.out.println(amount + " paid with credit/debit card.");
            return true;
        }
        return false;
    }
}
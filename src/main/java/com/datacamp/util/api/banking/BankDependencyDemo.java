package com.datacamp.util.api.banking;

public class BankDependencyDemo {
    private final EuropeanCentralBank europeanCentralBank;

    public BankDependencyDemo(EuropeanCentralBank europeanCentralBank) {
        this.europeanCentralBank = europeanCentralBank;
    }

    public double convertEuroTo(String currency, double amount) {
        double rate = this.europeanCentralBank.getRateEuroTo(currency);

        return amount * rate;
    }
}

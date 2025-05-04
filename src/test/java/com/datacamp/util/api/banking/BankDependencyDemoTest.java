package com.datacamp.util.api.banking;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BankDependencyDemoTest {

    @Test
    void convert_convertsWithoutError() {
        var europeanCentralBank = new EuropeanCentralBank();
        var bankDependencyDemo = new BankDependencyDemo(europeanCentralBank);
        var amount = 1000.0;
        var currency = "USD";

        var result = bankDependencyDemo.convertEuroTo(currency, amount);

        System.out.println("Converted amount: " + result);
        assertTrue(result > 0);
    }

    @Test
    void convert_cannotFindCurrency() {
        var europeanCentralBank = new EuropeanCentralBank();
        var bankDependencyDemo = new BankDependencyDemo(europeanCentralBank);
        var amount = 1000.0;
        var currency = "INVALID_CURRENCY";

        assertThrows(
                RuntimeException.class, () -> bankDependencyDemo.convertEuroTo(currency, amount));
    }
}

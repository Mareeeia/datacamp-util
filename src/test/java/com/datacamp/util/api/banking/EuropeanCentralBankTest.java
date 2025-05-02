package com.datacamp.util.api.banking;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EuropeanCentralBankTest {

    @ParameterizedTest
    @MethodSource("enumToString")
    void getRateEURto_returnsRates(String currency) throws Exception {
        var europeanCentralBank = new EuropeanCentralBank();

        var rate = europeanCentralBank.getRateEURto(currency);

        assertTrue(rate > 0);
    }

    private static List<String> enumToString() {
        return Arrays.stream(Currencies.values()).map(Currencies::toString).toList();
    }
}
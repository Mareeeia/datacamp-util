package com.datacamp.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.datacamp.util.testing.CustomJUnitTestLauncher.launchTestsAndPrint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Calculator {
    private final Dependency dependency;

    public Calculator(Dependency dependency) {
        this.dependency = dependency;
    }

    public int add(int a, int b) {
        return dependency.calculate(a, b); // Delegates to dependency
    }
}

class CalculatorTest {

    private Calculator calculator;
    private Dependency dependency;

    @BeforeEach
    void setUp() {
        dependency = Mockito.mock(Dependency.class);
        calculator = new Calculator(dependency);
    }

    @Test
    void testAddition_addsCorrectly() {
        when(dependency.calculate(2, 3)).thenReturn(5);

        int result = calculator.add(2, 3);

        assertEquals(5, result);
        verify(dependency).calculate(2, 3);
    }

    @Test
    void testAddition_addsIncorrectly() {
        when(dependency.calculate(2, 3)).thenReturn(6); // Wrong mock behavior

        int result = calculator.add(2, 3);

        assertNotEquals(5, result);
    }

    @Test
    void testAddition_throwsException() {
        when(dependency.calculate(2, 3)).thenThrow(new RuntimeException("Dependency failed"));

        calculator.add(2, 3);
    }
}

public class DemoClassWithTests {
    public static void main(String[] args) {
        launchTestsAndPrint(CalculatorTest.class);
    }
}

interface Dependency {
    int calculate(int a, int b);
}

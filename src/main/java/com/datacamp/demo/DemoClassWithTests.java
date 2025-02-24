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
    void testAddition() {
        when(dependency.calculate(2, 3)).thenReturn(5);

        int result = calculator.add(2, 3);

        assertEquals(5, result);
        verify(dependency).calculate(2, 3);
    }

    @Test
    void testAdditionFailure() {
        when(dependency.calculate(2, 3)).thenReturn(6); // Wrong mock behavior

        int result = calculator.add(2, 3);

        assertNotEquals(5, result);
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

package com.datacamp.util.testing;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.datacamp.util.testing.CustomJUnitTestLauncher.launchTestsAndPrint;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomJUnitTestLauncherTest {

    private static final String HEADER = "-------------------------------------------------------\n" +
            " T E S T S\n" +
            "-------------------------------------------------------";

    @Test
    void launchTestsAndPrint_printsOutput_whenTestsPresent() {
        var actual = getCapturedOutput(() -> launchTestsAndPrint(ClassWithTests.class));

        assertThat(actual, containsString(HEADER));
        assertThat(actual, containsString("Tests run: 1, Failures: 0, Errors: 0, Skipped: 0"));
    }

    @Test
    void launchTestsAndPrint_printsOutput_whenNoTestsPresent() {
        var actual = getCapturedOutput(() -> launchTestsAndPrint(ClassWithNoTests.class));

        assertThat(actual, containsString(HEADER));
        assertThat(actual, containsString("Tests run: 0, Failures: 0, Errors: 0, Skipped: 0"));
    }

    @Test
    void launchTestsAndPrint_printsOutput_whenTestsFail() {
        var actual = getCapturedOutput(() -> launchTestsAndPrint(ClassWithFailedTests.class));

        assertThat(actual, containsString(HEADER));
        assertThat(actual, containsString("Tests run: 1, Failures: 1, Errors: 0, Skipped: 0"));
    }

    @Test
    void launchTestsAndPrint_printsOutput_whenExceptionThrown() {
        var actual = getCapturedOutput(() -> launchTestsAndPrint(ClassThrowsExceptionTest.class));

        assertThat(actual, containsString(HEADER));
        assertThat(actual, containsString("Tests run: 1, Failures: 0, Errors: 1, Skipped: 0"));
    }

    private String getCapturedOutput(Runnable runnable) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);

        try {
            System.setOut(newOut);

            runnable.run();
        } finally {
            System.setOut(originalOut);
        }

        return baos.toString();
    }
}

class ClassWithNoTests {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}

class ClassWithTests {
    @Test
    void testMethod() {
        assertTrue(true);
    }
}

class ClassWithFailedTests {
    @Test
    void testMethod() {
        assertTrue(false);
    }
}

class ClassThrowsExceptionTest {
    @Test
    void testMethod() {
        throw new RuntimeException("Test exception");
    }
}
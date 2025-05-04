package com.datacamp.util.testing;

import static com.datacamp.util.testing.CustomJUnitTestLauncher.launchTestsAndPrint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class CustomJUnitTestLauncherTest {

    private static final String HEADER =
            "-------------------------------------------------------\n"
                    + " T E S T S\n"
                    + "-------------------------------------------------------";

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
        assertThat(
                actual,
                containsString(
                        "ClassWithFailedTests.testMethod:86 expected: <true> but was: <false>"));
    }

    @Test
    void launchTestsAndPrint_printsOutput_whenExceptionThrown() {
        var actual = getCapturedOutput(() -> launchTestsAndPrint(ClassThrowsExceptionTest.class));

        assertThat(actual, containsString(HEADER));
        assertThat(actual, containsString("Tests run: 1, Failures: 0, Errors: 1, Skipped: 0"));
        assertThat(actual, containsString("ClassThrowsExceptionTest.testMethod:93 Test exception"));
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

// Expected to fail
class ClassWithFailedTests {
    @Test
    void testMethod() {
        assertTrue(false);
    }
}

// Expected to fail
class ClassThrowsExceptionTest {
    @Test
    void testMethod() {
        throw new RuntimeException("Test exception");
    }
}

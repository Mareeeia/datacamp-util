package com.datacamp.util.exceptions;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisplayExceptionsTest {

    public static final String THREAD_NAME = "thread";

    @Test
    void displayExceptions_displayException_whenNonempty() throws InterruptedException {
        String actual = captureErrorOutputThreaded(() -> DisplayExceptions.displayExceptions(() -> {
            throw new RuntimeException("Runtime Exception");
        }));

        assertFalse(actual.isEmpty());
    }

    @Test
    void displayExceptions_displayNoException_whenEmpty() throws InterruptedException {
        String actual = captureErrorOutputThreaded(() -> DisplayExceptions.displayExceptions(() -> {
            System.out.println("No exception");
        }));

        assertTrue(actual.isEmpty());
    }

    @Test
    void displayExceptions_displaysAssertionErrorLikeThrown() throws InterruptedException {
        String expected = captureErrorOutputThreaded(() -> {
            throw new AssertionError("AssertionError");
        });

        String actual = captureErrorOutputThreaded(() -> DisplayExceptions.displayExceptions(() -> {
            throw new AssertionError("AssertionError");
        }));

        compareOutput(expected, actual);
    }

    @Test
    void displayExceptions_displaysAssertionFailedErrorLikeThrown() throws InterruptedException {
        String expected = captureErrorOutputThreaded(() -> {
            throw new AssertionFailedError("Expected and actual differ");
        });

        String actual = captureErrorOutputThreaded(() -> DisplayExceptions.displayExceptions(() -> {
            throw new AssertionFailedError("Expected and actual differ");
        }));

        compareOutput(expected, actual);
    }

    @Test
    void displayExceptions_displaysNPELikeThrown() throws InterruptedException {
        String expected = captureErrorOutputThreaded(() -> {
            throw new NullPointerException("This is a NullPointerException");
        });

        String actual = captureErrorOutputThreaded(() -> DisplayExceptions.displayExceptions(() -> {
            throw new NullPointerException("This is a NullPointerException");
        }));

        compareOutput(expected, actual);
    }

    private String captureErrorOutputThreaded(Runnable runnable) throws InterruptedException {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newErr = new PrintStream(baos);

        System.setErr(newErr);

        Thread t = new Thread(runnable);
        t.setName(THREAD_NAME);
        t.start();

        t.join();

        System.setErr(originalErr);
        return baos.toString();
    }

    private void compareOutput(String expected, String actual) {
        var expectedLines = splitAndRemoveLineNumbers(expected);
        var actualLines = splitAndRemoveLineNumbers(actual);

        assertThat(actualLines, containsInAnyOrder(expectedLines.toArray()));
    }

    private Set<String> splitAndRemoveLineNumbers(String output) {
        return Arrays.stream(output.split("\n"))
                .map(line -> line.replaceAll("\\d", ""))
                .collect(Collectors.toSet());
    }
}
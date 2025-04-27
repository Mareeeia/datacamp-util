package com.datacamp.util.testing;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Prints test–by–test output that looks like Maven Surefire.
 * <p>
 * Sample lines:
 * Running com.acme.FooTest
 * barShouldDoBaz  Time elapsed 0.003 s  <<< FAILURE!
 * java.lang.AssertionError: expected:<1> but was:<0>
 * at ...FooTest.java:42
 */
public final class MavenStyleTestExecutionListener implements TestExecutionListener {

    private static final DecimalFormat TIME_FMT = new DecimalFormat("0.000");

    /**
     * nanoTime when each test started
     */
    private final Map<TestIdentifier, Long> started = new HashMap<>();

    /**
     * track whether we have already printed the “Running …” line for this container
     */
    private boolean printedRunning = false;

    @Override
    public void executionStarted(TestIdentifier id) {
        // Class header
        if (id.isContainer() && id.getSource().filter(ClassSource.class::isInstance).isPresent()) {
            ClassSource cs = (ClassSource) id.getSource().get();
            if (!printedRunning) {
                System.out.println("Running " + cs.getClassName());
                printedRunning = true;
            }
        }

        if (id.isTest()) {
            started.put(id, System.nanoTime());
        }
    }

    @Override
    public void executionFinished(TestIdentifier id, TestExecutionResult result) {
        if (!id.isTest()) return;

        double elapsed = nanosToSeconds(started.get(id));
        String testName = id.getDisplayName();
        String tail = "";                            // SUCCESS path

        if (result.getStatus() != TestExecutionResult.Status.SUCCESSFUL) {
            tail = result.getStatus() == TestExecutionResult.Status.FAILED
                    ? "  <<< FAILURE!"
                    : "  <<< ERROR!";
        }

        System.out.printf("%s  Time elapsed %s s%s%n",
                testName, TIME_FMT.format(elapsed), tail);

        result.getThrowable().ifPresent(t -> t.printStackTrace(System.out));
    }

    private static double nanosToSeconds(Long start) {
        return start == null ? 0D : (System.nanoTime() - start) / 1_000_000_000D;
    }
}

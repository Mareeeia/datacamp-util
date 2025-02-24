package com.datacamp.util.testing;

import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class CustomJUnitTestLauncher {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void launchTestsAndPrint(Class<?> testClass) {
        System.out.println("-------------------------------------------------------");
        System.out.println(" T E S T S");
        System.out.println("-------------------------------------------------------");

        var summary = executeTests(testClass);

        printSummary(summary);

        System.out.println("-------------------------------------------------------");
    }

    private static TestExecutionSummary executeTests(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(new MavenStyleTestExecutionListener(ClassSource.from(testClass)), summaryListener);
        launcher.execute(request);

        return summaryListener.getSummary();
    }

    private static void printSummary(TestExecutionSummary summary) {
        long testsFoundCount = summary.getTestsFoundCount();
        long testsSkipped = summary.getTestsSkippedCount();
        long testsFailed = summary.getTestsFailedCount();
        long testsErrored = summary.getFailures().stream()
                .filter(failure -> failure.getException().toString().contains("Exception"))
                .count();
        testsFailed -= testsErrored;

        String color = (testsFailed > 0) ? ANSI_RED : ANSI_GREEN;

        System.out.println("Results:");
        System.out.printf("%sTests run: %d, Failures: %d, Errors: %d, Skipped: %d%s%n",
                color, testsFoundCount, testsFailed, testsErrored, testsSkipped, ANSI_RESET);

        if (testsFailed > 0) {
            System.out.println(ANSI_RED + "Some tests FAILED." + ANSI_RESET);
        } else {
            System.out.println(ANSI_GREEN + "All tests passed!" + ANSI_RESET);
        }
    }
}


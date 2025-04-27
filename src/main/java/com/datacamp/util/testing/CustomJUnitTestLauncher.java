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

        System.out.println("Results:");
        System.out.printf("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d%n",
                testsFoundCount, testsFailed, testsErrored, testsSkipped);

        if (testsFailed + testsErrored > 0) {
            System.out.println("There are test failures.");
        } else {
            System.out.println("All tests passed!");
        }
    }
}


package com.datacamp.util.testing;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.HashSet;
import java.util.Set;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class CustomJUnitTestLauncher {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void launchTestsAndPrint(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();

        Launcher launcher = LauncherFactory.create();

        MavenStyleTestExecutionListener customListener = new MavenStyleTestExecutionListener(ClassSource.from(testClass));

        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(customListener, summaryListener);

        launcher.execute(request);

        TestExecutionSummary summary = summaryListener.getSummary();

        long testsFoundCount = summary.getTestsFoundCount();
        long testsSkipped = summary.getTestsSkippedCount();
        long testsAborted = summary.getTestsAbortedCount();
        long testsFailed = summary.getTestsFailedCount();
        long testsSucceeded = summary.getTestsSucceededCount();

        String color = (testsFailed > 0) ? ANSI_RED : ANSI_GREEN;

        System.out.println("Results:");
        System.out.printf("%sTests run: %d, Failures: %d, Errors: 0, Skipped: %d%s%n",
                color, testsFoundCount, testsFailed, testsSkipped, ANSI_RESET);

        if (testsFailed > 0) {
            System.out.println(ANSI_RED + "Some tests FAILED." + ANSI_RESET);
        } else {
            System.out.println(ANSI_GREEN + "All tests passed!" + ANSI_RESET);
        }
    }

    static class MavenStyleTestExecutionListener implements TestExecutionListener {
        private final Set<String> runningTestClasses = new HashSet<>();
        private final ClassSource classSource;

        public MavenStyleTestExecutionListener(ClassSource classSource) {
            this.classSource = classSource;
        }

        @Override
        public void executionStarted(TestIdentifier testIdentifier) {
            if (testIdentifier.isContainer()) {
                String className = classSource.getClassName();
                if (!runningTestClasses.contains(className)) {
                    System.out.println("Running " + className);
                    runningTestClasses.add(className);
                }
            } else if (testIdentifier.isTest()) {
                System.out.println("  -> " + testIdentifier.getDisplayName() + " STARTED");
            }
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            if (testIdentifier.isTest()) {
                switch (testExecutionResult.getStatus()) {
                    case SUCCESSFUL:
                        System.out.println("  -> " + ANSI_GREEN + testIdentifier.getDisplayName() + " PASSED" + ANSI_RESET);
                        break;
                    case FAILED:
                        System.out.println("  -> " + ANSI_RED + testIdentifier.getDisplayName() + " FAILED" + ANSI_RESET);
                        Throwable t = testExecutionResult.getThrowable().orElse(null);
                        if (t != null) {
                            System.out.println("     " + t.getClass().getName() + ": " + t.getMessage());
                        }
                        break;
                    case ABORTED:
                        // Typically from assumptions, or @Disabled in older versions, or dynamic tests
                        // In Surefire, "skipped" might be the terminology
                        System.out.println("  -> " + ANSI_YELLOW + testIdentifier.getDisplayName() + " SKIPPED/ABORTED" + ANSI_RESET);
                        break;
                }
            }
        }
    }
}


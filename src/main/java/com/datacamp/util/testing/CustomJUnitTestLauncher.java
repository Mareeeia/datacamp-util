package com.datacamp.util.testing;

import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class CustomJUnitTestLauncher {

    public static void launchTestsAndPrint(Class<?> testClass) {
        System.out.println("-------------------------------------------------------");
        System.out.println(" T E S T S");
        System.out.println("-------------------------------------------------------");

        var summary = executeTests(testClass);

        printSummary(summary, testClass);

        System.out.println("-------------------------------------------------------");
    }

    private static TestExecutionSummary executeTests(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(
                new MavenStyleTestExecutionListener(),
                summaryListener);
        launcher.execute(request);

        return summaryListener.getSummary();
    }

    private static void printSummary(TestExecutionSummary summary, Class<?> testClass) {
        long testsRun = summary.getTestsFoundCount();
        long skipped = summary.getTestsSkippedCount();

        List<TestExecutionSummary.Failure> failures = summary.getFailures();

        long failureCnt = failures.stream()
                .filter(f -> f.getException() instanceof AssertionError)
                .count();
        long errorCnt = failures.size() - failureCnt;

        double timeSec = (summary.getTimeFinished() - summary.getTimeStarted()) / 1_000.0;

        System.out.printf("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %.3f s - in %s%n",
                testsRun, failureCnt, errorCnt, skipped, timeSec, testClass.getName());
        System.out.println();

        // Detailed summary section like Maven Surefire
        printFailureDetails(failures);

        System.out.println("Results:");
        System.out.printf("Tests run: %d, Failures: %d, Errors: %d, Skipped: %d%n",
                testsRun, failureCnt, errorCnt, skipped);

        if (failureCnt + errorCnt > 0) {
            System.out.println("There are test failures.");
        } else {
            System.out.println("All tests passed!");
        }
    }

    private static void printFailureDetails(List<TestExecutionSummary.Failure> failures) {
        List<TestExecutionSummary.Failure> assertionFailures = failures.stream()
                .filter(f -> f.getException() instanceof AssertionError)
                .toList();
        List<TestExecutionSummary.Failure> otherErrors = failures.stream()
                .filter(f -> !(f.getException() instanceof AssertionError))
                .toList();

        if (!assertionFailures.isEmpty()) {
            System.out.println("Failures:");
            assertionFailures.forEach(f -> System.out.println("   " + formatFailure(f)));
        }
        if (!otherErrors.isEmpty()) {
            System.out.println("Errors:");
            otherErrors.forEach(f -> System.out.println("   " + formatFailure(f)));
        }
    }

    private static String formatFailure(TestExecutionSummary.Failure failure) {
        String className = "UnknownClass";
        String methodName = "unknownMethod";
        int line = -1;

        if (failure.getTestIdentifier().getSource().isPresent()) {
            var source = failure.getTestIdentifier().getSource().get();
            if (source instanceof MethodSource ms) {
                className = ms.getJavaClass().getSimpleName();
                methodName = ms.getMethodName();
            } else if (source instanceof ClassSource cs) {
                className = cs.getJavaClass().getSimpleName();
            }
        }

        for (StackTraceElement ste : failure.getException().getStackTrace()) {
            if (ste.getClassName().contains(className)) {
                line = ste.getLineNumber();
                break;
            }
        }

        String message = failure.getException().getMessage();
        return String.format("%s.%s:%d %s", className, methodName, line, message);
    }
}


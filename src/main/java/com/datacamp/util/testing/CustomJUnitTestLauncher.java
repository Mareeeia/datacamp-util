package com.datacamp.util.testing;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.time.Duration;

public class CustomJUnitTestLauncher {

    public static void launchTestsAndPrint(Class<?> clazz) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(clazz))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.execute(request, listener);

        TestExecutionSummary summary = listener.getSummary();

        customPrintSummary(summary, new PrintWriter(System.out));

        if (!summary.getFailures().isEmpty()) {
            summary.getFailures().forEach(failure ->
                sneakyThrow(failure.getException())
            );
        }
    }

    private static void customPrintSummary(TestExecutionSummary summary, PrintWriter writer) {
        writer.printf("%nTest run finished after %d ms%n"

                        + "[%10d tests found           ]%n"
                        + "[%10d tests skipped         ]%n"
                        + "[%10d tests started         ]%n"
                        + "[%10d tests aborted         ]%n"
                        + "[%10d tests successful      ]%n"
                        + "[%10d tests failed          ]%n"
                        + "%n",

                Duration.ofNanos(summary.getTimeStarted() - summary.getTimeFinished()).toMillis(),

                summary.getTestsFoundCount(),
                summary.getTestsSkippedCount(),
                summary.getTestsStartedCount(),
                summary.getTestsAbortedCount(),
                summary.getTestsSucceededCount(),
                summary.getTestsFailedCount()
        );

        writer.flush();
    }

    public static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
        throw (T) t; // This forces the compiler to ignore checked exceptions
    }
}

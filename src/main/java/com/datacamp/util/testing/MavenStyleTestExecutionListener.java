package com.datacamp.util.testing;

import com.datacamp.util.exceptions.DisplayExceptions;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.util.Set;

class MavenStyleTestExecutionListener implements TestExecutionListener {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private final Set<String> runningTestClasses;
    private final ClassSource classSource;

    public MavenStyleTestExecutionListener(ClassSource classSource) {
        this.classSource = classSource;
        this.runningTestClasses = new java.util.HashSet<>();
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
                    System.out.println("  -> " + testIdentifier.getDisplayName() + ANSI_GREEN + " PASSED" + ANSI_RESET);
                    break;
                case FAILED:
                    System.out.println("  -> " + testIdentifier.getDisplayName() + ANSI_RED + " FAILED" + ANSI_RESET);
                    testExecutionResult.getThrowable().ifPresent(DisplayExceptions::printExceptionLikeJVM);
                    break;
                case ABORTED:
                    System.out.println("  -> " + testIdentifier.getDisplayName() + ANSI_YELLOW + " SKIPPED/ABORTED" + ANSI_RESET);
                    break;
            }
        }
    }
}
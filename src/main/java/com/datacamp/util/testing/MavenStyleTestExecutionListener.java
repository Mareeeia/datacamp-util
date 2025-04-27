package com.datacamp.util.testing;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.util.Set;

class MavenStyleTestExecutionListener implements TestExecutionListener {

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
                    System.out.println("  -> " + testIdentifier.getDisplayName() + " PASSED");
                    break;
                case FAILED:
                    System.out.println("  -> " + testIdentifier.getDisplayName() + " FAILED");
                    break;
                case ABORTED:
                    System.out.println("  -> " + testIdentifier.getDisplayName() + " SKIPPED/ABORTED");
                    break;
            }
        }
    }
}
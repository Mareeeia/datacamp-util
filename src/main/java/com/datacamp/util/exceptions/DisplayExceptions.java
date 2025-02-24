package com.datacamp.util.exceptions;

public class DisplayExceptions {

    public static void displayExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            printExceptionLikeJVM(t);
        }
    }

    private static void printExceptionLikeJVM(Throwable t) {
        System.err.println("Exception in thread \""
                + Thread.currentThread().getName() + "\" " + t);
        for (StackTraceElement element : t.getStackTrace()) {
            if (!element.toString().contains("DisplayExceptions.displayExceptions")) {
                System.err.println("\tat " + element);
            }
        }
    }
}

package com.datacamp.demo;


import static com.datacamp.util.exceptions.DisplayExceptions.displayExceptions;

public class DemoExceptions {

    public static void main(String[] args) {
        displayExceptions(DemoExceptions::method);

        System.out.println(" ^");
        System.out.println(" | This line is executed after the exception is caught.");
        System.out.println(" | As you can see, control flow has not been disrupted by the exception.");
    }

    public static void method() {
        int[] arr = new int[5];
        arr[10] = 10;
    }
}

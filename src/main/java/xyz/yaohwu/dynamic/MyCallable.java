package xyz.yaohwu.dynamic;

import java.util.concurrent.Callable;

public class MyCallable implements Callable<String> {
    @Override
    public String call() {
        System.out.println("callable called");
        return "null";
    }
}
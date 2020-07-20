package xyz.yaohwu.dynamic;


import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CallInterceptor {

    @RuntimeType
    public static Object intercept(
            @This(optional = true) Object origin,
            @Origin(cache = false) Method method,
            @SuperCall Callable<?> callable,
            @AllArguments Object[] args
    ) throws Exception {
        System.out.println("intercept call");
        return callable.call();
    }
}
package xyz.yaohwu.github.i1364;


import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class I1364CallInterceptor {

    @RuntimeType
    public static Object intercept(@This(optional = true) Object origin,
                                   @Origin(cache = false) Method method,
                                   @SuperCall Callable<?> callable) throws Exception {
        System.out.println("intercept call");
        return callable.call();
    }
}
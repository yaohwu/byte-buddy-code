package xyz.yaohwu.github.demo;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.concurrent.Callable;

/**
 * @author yaohwu
 * created by yaohwu at 2022/11/3 10:39
 */
public class EnvironmentSettingInterceptor {
    @RuntimeType
    public static Object intercept(
            @This(optional = true) Object target,
            @SuperCall Callable<?> callable) throws Exception {
        // some logical
        System.out.println("xxxxxx");
        return callable.call();
    }
}

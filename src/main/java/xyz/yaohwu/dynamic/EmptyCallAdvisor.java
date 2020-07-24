package xyz.yaohwu.dynamic;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/24 20:01
 */
public class EmptyCallAdvisor {
    @Advice.OnMethodEnter
    public static void onMethodEnter(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object origin,
                                     @Advice.Origin Method method,
                                     @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] arguments) {
        // do nothing
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onMethodExit(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object origin,
                                    @Advice.Origin Method method,
                                    @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] arguments,
                                    @Advice.Return(readOnly = true, typing = Assigner.Typing.DYNAMIC) Object ret,
                                    @Advice.Thrown(readOnly = true, typing = Assigner.Typing.DYNAMIC) Throwable e) {
        // do nothing
    }
}

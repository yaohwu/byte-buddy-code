package xyz.yaohwu.dynamic;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/16 11:30
 */
public class CallAdvisor {


    @Advice.OnMethodEnter
    public static String onMethodEnter(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object origin,
                                       @Advice.Origin Method method,
                                       @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] arguments) {

        System.out.println("call enter");
        System.out.println(new MyCallable().call());
        return "message returned by method enter";
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onMethodExit(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object origin,
                                    @Advice.Origin Method method,
                                    @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] arguments,
                                    @Advice.Return(readOnly = true, typing = Assigner.Typing.DYNAMIC) Object ret,
                                    @Advice.Thrown(readOnly = true, typing = Assigner.Typing.DYNAMIC) Throwable e,
                                    @Advice.Enter(typing = Assigner.Typing.DYNAMIC) String returnByEnter) {
        if (e != null) {
            System.out.println(e.getMessage());
        }
        System.out.println("return by enter is that: " + returnByEnter);
        System.out.println("call left");
    }
}

package xyz.yaohwu.github.i1364;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yaohwu
 * created by yaohwu at 2022/11/24 09:43
 */
public class MainForI1364 {

    public static void main(String[] args) throws IOException {

        new ByteBuddy()
                .rebase(MyClassTest.class)
                .name("xyz.yaohwu.github.i1364.MyClassTestRebase")
                .method(ElementMatchers.named("test"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .saveIn(new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code"));


        try {
            Object test = new ByteBuddy()
                    .rebase(MyClassTest.class)
                    .method(ElementMatchers.named("test"))
                    .intercept(FixedValue.value("Hello World!"))
                    .make().load(String.class.getClassLoader()).getLoaded().newInstance();
            // origin method
            Method originMethod = test.getClass().getDeclaredMethods()[0];
            System.out.println("method: " + originMethod.getName());
            originMethod.setAccessible(true);
            // origin method call
            System.out.println(originMethod.invoke(test));

            // origin method
            Method testMethod = test.getClass().getDeclaredMethods()[1];
            System.out.println("method: " + testMethod.getName());
            testMethod.setAccessible(true);
            // origin method call
            System.out.println(testMethod.invoke(test));

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        new ByteBuddy()
                .redefine(MyClassTest.class)
                .name("xyz.yaohwu.github.i1364.MyClassTestRedefine")
                .method(ElementMatchers.named("test"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .saveIn(new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code"));

        // last two result content is same for

        // The difference comes when you try to invoke an existing method, such as by SuperMethodCall.
        // With rebase, the original method is treated as if it was declared by a super class.
        // With redefine, it is as if the method did not exist.

        new ByteBuddy()
                .rebase(MyClassTest.class)
                .name("xyz.yaohwu.github.i1364.MyClassTestDelegationRebase")
                .visit(Advice.to(I1364CallAdvisor.class).on(ElementMatchers.named("test")))
                .make()
                .saveIn(new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code"));

        new ByteBuddy()
                .redefine(MyClassTest.class)
                .name("xyz.yaohwu.github.i1364.MyClassTestDelegationRedefine")
                .visit(Advice.to(I1364CallAdvisor.class).on(ElementMatchers.named("test")))
                .make()
                .saveIn(new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code"));


        new ByteBuddy()
                .rebase(MyClassTest.class)
                .name("xyz.yaohwu.github.i1364.MyClassTestDelegationSuperCallRebase")
                .method(ElementMatchers.named("test"))
                .intercept(SuperMethodCall.INSTANCE)
                .make()
                .saveIn(new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code"));


        new ByteBuddy()
                // using SuperMethodCall.INSTANCE in redefine mode got error
                .redefine(MyClassTest.class)
                .name("xyz.yaohwu.github.i1364.MyClassTestDelegationSuperCallRedefine")
                .method(ElementMatchers.named("test"))
                .intercept(SuperMethodCall.INSTANCE)
                .make()
                .saveIn(new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code"));

    }
}

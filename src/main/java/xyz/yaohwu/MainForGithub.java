package xyz.yaohwu;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.yaohwu.github.demo.EnvironmentSettingInterceptor;
import xyz.yaohwu.github.demo.StreamExecutionEnvironment;
import xyz.yaohwu.tool.InstrumentationProviderImpl;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesNoArguments;

/**
 * @author yaohwu
 * created by yaohwu at 2022/11/3 10:21
 */
public class MainForGithub {


    public static void main(String[] args) {

        // comment this line, and it will work
//        System.out.println(StreamExecutionEnvironment.getExecutionEnvironment().desc());

        AgentBuilder.Default iBuilder = new AgentBuilder.Default();
        iBuilder.type(

                        ElementMatchers.named(
                                "xyz.yaohwu.github.demo.StreamExecutionEnvironment"))
                .transform(
                        (builder, typeDescription, classLoader, javaModule) -> builder.method(
                                        named("getExecutionEnvironment")
                                                .and(takesNoArguments()))
                                .intercept(
                                        MethodDelegation.to(EnvironmentSettingInterceptor.class)))

                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()))
                .installOn(InstrumentationProviderImpl.INSTANCE.findInstrumentation());

        System.out.println(StreamExecutionEnvironment.getExecutionEnvironment().desc());

    }


}

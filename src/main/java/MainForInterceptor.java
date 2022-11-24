import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import xyz.yaohwu.another.world.Person;
import xyz.yaohwu.dynamic.CallInterceptor;
import xyz.yaohwu.tool.InstrumentationProvider;
import xyz.yaohwu.tool.InstrumentationProviderImpl;
import xyz.yaohwu.tool.OutputContent;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author yaohwu
 * created by yaohwu at 2022/3/10 12:04
 */
public class MainForInterceptor {

    private static final InstrumentationProvider provider = InstrumentationProviderImpl.INSTANCE;

    public static void main(String[] args) {
        Instrumentation instrumentation = provider.findInstrumentation();

        System.out.println("before agent.");
        // never call new Persion() for loading Person.class and initializing Person.class to make interceptor work
//        new Person().say();

        AgentBuilder.Identified.Extendable iExtendable = null;
        ResettableClassFileTransformer iResettable = null;
        AgentBuilder.Default iBuilder = new AgentBuilder.Default();
        iExtendable = iBuilder.type(named("xyz.yaohwu.another.world.Person"))
                .transform((builder, typeDescription, classLoader, module) -> {
                    builder = builder.method(named("say")).intercept(MethodDelegation.to(CallInterceptor.class));
                    return builder;
                });
        iResettable = iExtendable.with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()) {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        super.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                        OutputContent.output(typeDescription, dynamicType, "interceptor");
                    }
                })
                // this code block is useless.
//                .disableClassFormatChanges()
//                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REBASE)
                .installOn(instrumentation);


        System.out.println("after interceptor agent used.");
        new Person().say();

        Main.showLoadedClass(instrumentation);
    }
}


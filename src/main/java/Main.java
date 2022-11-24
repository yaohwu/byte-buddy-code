import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Listener;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import xyz.yaohwu.another.world.Person;
import xyz.yaohwu.dynamic.CallAdvisor;
import xyz.yaohwu.dynamic.CallInterceptor;
import xyz.yaohwu.tool.InstrumentationProvider;
import xyz.yaohwu.tool.InstrumentationProviderImpl;
import xyz.yaohwu.tool.OutputContent;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/14 10:53
 */
public class Main {

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
        iResettable = iExtendable.with(new Listener.WithTransformationsOnly(Listener.StreamWriting.toSystemOut()) {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        super.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                        OutputContent.output(typeDescription, dynamicType, "interceptor");
                    }
                })
                // this code block is useless.
//                .disableClassFormatChanges()
//                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
//                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
//                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);


        System.out.println("after interceptor agent used.");
        new Person().say();

        showLoadedClass(instrumentation);

        // this code block is useless.
//        if (iResettable != null) {
//            iResettable.reset(instrumentation,
//                    AgentBuilder.RedefinitionStrategy.REDEFINITION,
//                    AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE,
//                    AgentBuilder.RedefinitionStrategy.BatchAllocator.ForFixedSize.ofSize(1),
//                    AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemOut()
//            );
//        }

//        System.out.println("after reset interceptor agent.");
//        new Person().say();


        AgentBuilder.Identified.Extendable extendable = null;
        ResettableClassFileTransformer resettable = null;
        AgentBuilder.Default builder = new AgentBuilder.Default();
        extendable = builder.type(named("xyz.yaohwu.another.world.Person"))
                .transform((innerBuilder, typeDescription, classLoader, module) -> {
                    innerBuilder = innerBuilder.visit(
                            Advice.to(CallAdvisor.class)
                                    .on(named("say")));
                    return innerBuilder;
                });
        resettable = extendable.with(new Listener.WithTransformationsOnly(Listener.StreamWriting.toSystemOut()) {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        super.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                        OutputContent.output(typeDescription, dynamicType, "advisor");
                    }
                })
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);


        System.out.println("after advisor agent used");
        new Person().say();

        showLoadedClass(instrumentation);

        if (resettable != null) {
            resettable.reset(instrumentation,
                    AgentBuilder.RedefinitionStrategy.RETRANSFORMATION,
                    AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE,
                    AgentBuilder.RedefinitionStrategy.BatchAllocator.ForFixedSize.ofSize(1),
                    AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemOut()
            );
        }

        System.out.println("after advisor agent reset");
        new Person().say();
    }

    public static void showLoadedClass(Instrumentation instrumentation) {
        for (Class<?> aClass : instrumentation.getAllLoadedClasses()) {
            String name = aClass.getName();
            if (name.startsWith("xyz.yaohwu.another.world")) {
                System.out.println(name + " loaded after agent");
            }
        }
    }
}

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import xyz.yaohwu.anno.Monitor;
import xyz.yaohwu.anno.MonitorSub;
import xyz.yaohwu.another.world.Bike;
import xyz.yaohwu.dynamic.CallAdvisor;
import xyz.yaohwu.dynamic.EmptyCallAdvisor;
import xyz.yaohwu.tool.InstrumentationProvider;
import xyz.yaohwu.tool.InstrumentationProviderImpl;
import xyz.yaohwu.tool.OutputContent;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/24 18:06
 */
public class MainForCircularity {
    private static final InstrumentationProvider provider = InstrumentationProviderImpl.INSTANCE;

    public static void main(String[] args) {
        Instrumentation instrumentation = provider.findInstrumentation();

        System.out.println("make sure Bike loaded and Wheel not loaded before agent used.");
        new Bike();
        showLoadedClass(instrumentation);

        // if you comment out this line, the Bike$Wheel#say() will not be advised by actual @{code CallAdvisor}
        emptyAdvisorJustForClassLoad(instrumentation);
        // if you comment out this line, the Bike$Wheel#WheelInner#say() will not be advised by actual @{code CallAdvisor}
        emptyAdvisorJustForClassLoad(instrumentation);

        System.out.println("after empty");
        showLoadedClass(instrumentation);

        AgentBuilder.Identified.Extendable extendable = null;
        ResettableClassFileTransformer resettable = null;
        AgentBuilder.Default builder = new AgentBuilder.Default();
        extendable = builder
                .type(nameStartsWith("xyz.yaohwu.another.world.")
                        .and(target -> isAnnotatedWith(Monitor.class).matches(target)))
                .transform((innerBuilder, typeDescription, classLoader, module) -> {
                    innerBuilder = innerBuilder.visit(
                            Advice.to(CallAdvisor.class)
                                    .on(isAnnotatedWith(Monitor.class)));
                    return innerBuilder;
                });
        extendable = extendable
                .type(nameStartsWith("xyz.yaohwu.another.world.")
                        .and(target -> isAnnotatedWith(MonitorSub.class).matches(target)))
                .transform((innerBuilder, typeDescription, classLoader, module) -> {
                    innerBuilder = innerBuilder.visit(
                            Advice.to(CallAdvisor.class)
                                    .on(isAnnotatedWith(MonitorSub.class)));
                    return innerBuilder;
                });
        resettable = extendable
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()) {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        super.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                        outputClassContentToFile(typeDescription, dynamicType);
                    }
                })
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);


        System.out.println("after advisor agent used");
        new Bike().say();

        showLoadedClass(instrumentation);
    }

    private static void emptyAdvisorJustForClassLoad(Instrumentation instrumentation) {
        AgentBuilder.Identified.Extendable extendable = null;
        ResettableClassFileTransformer resettable = null;
        AgentBuilder.Default builder = new AgentBuilder.Default();
        extendable = builder
                .type(nameStartsWith("xyz.yaohwu.another.world.")
                        .and(target -> isAnnotatedWith(Monitor.class).matches(target)))
                .transform((innerBuilder, typeDescription, classLoader, module) -> {
                    innerBuilder = innerBuilder.visit(
                            Advice.to(EmptyCallAdvisor.class)
                                    .on(isAnnotatedWith(Monitor.class)));
                    return innerBuilder;
                });
        extendable = extendable
                .type(nameStartsWith("xyz.yaohwu.another.world.")
                        .and(target -> isAnnotatedWith(MonitorSub.class).matches(target)))
                .transform((innerBuilder, typeDescription, classLoader, module) -> {
                    innerBuilder = innerBuilder.visit(
                            Advice.to(EmptyCallAdvisor.class)
                                    .on(isAnnotatedWith(MonitorSub.class)));
                    return innerBuilder;
                });
        resettable = extendable
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting.toSystemOut()) {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
                        super.onTransformation(typeDescription, classLoader, module, loaded, dynamicType);
                        outputClassContentToFile(typeDescription, dynamicType);
                    }
                })
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .installOn(instrumentation);
    }

    private static void showLoadedClass(Instrumentation instrumentation) {
        for (Class<?> aClass : instrumentation.getAllLoadedClasses()) {
            String name = aClass.getName();
            if (name.startsWith("xyz.yaohwu.another.world")) {
                System.out.println(name + " loaded");
            }
        }
    }

    private static void outputClassContentToFile(TypeDescription typeDescription, DynamicType dynamicType) {
        OutputContent.output(typeDescription.getSimpleName(), dynamicType.getBytes());
    }
}

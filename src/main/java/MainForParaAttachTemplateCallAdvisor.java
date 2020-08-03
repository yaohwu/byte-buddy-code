import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import xyz.yaohwu.anno.Monitor;
import xyz.yaohwu.another.world.Bike;
import xyz.yaohwu.dynamic.VersionSupporterImpl;
import xyz.yaohwu.tool.InstrumentationProvider;
import xyz.yaohwu.tool.InstrumentationProviderImpl;
import xyz.yaohwu.tool.OutputClassFileContent;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/24 18:06
 */
public class MainForParaAttachTemplateCallAdvisor {
    private static final InstrumentationProvider provider = InstrumentationProviderImpl.INSTANCE;

    public static void main(String[] args) {
        Instrumentation instrumentation = provider.findInstrumentation();

        System.out.println("make sure Bike loaded and Wheel not loaded before agent used.");
        new Bike();
        showLoadedClass(instrumentation);

        // if you comment out this line, the Bike$Wheel#say() will not be advised by actual @{code CallAdvisor}
        ResettableClassFileTransformer resettableClassFileTransformer = emptyAdvisorJustForClassLoad(instrumentation);

        System.out.println("after advisor agent used");
        // must output {@code cool}
        new Bike().say();

        showLoadedClass(instrumentation);

        if (resettableClassFileTransformer != null) {
            resettableClassFileTransformer.reset(instrumentation,
                    AgentBuilder.RedefinitionStrategy.RETRANSFORMATION,
                    AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE,
                    AgentBuilder.RedefinitionStrategy.BatchAllocator.ForFixedSize.ofSize(1),
                    AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemOut()
            );
        }
        System.out.println("after advisor agent reset");
        // must not output {@code cool}
        new Bike().say();

    }

    private static ResettableClassFileTransformer emptyAdvisorJustForClassLoad(Instrumentation instrumentation) {
        AgentBuilder.Identified.Extendable extendable = null;
        ResettableClassFileTransformer resettable = null;
        AgentBuilder.Default builder = new AgentBuilder.Default();
        extendable = builder
                .type(nameStartsWith("xyz.yaohwu.another.world.")
                        .and(target -> isAnnotatedWith(Monitor.class).matches(target)))
                .transform((innerBuilder, typeDescription, classLoader, module) -> {
                    innerBuilder = innerBuilder.visit(
                            Advice.to(VersionSupporterImpl.EMPTY.getAdvisorTypeDesc(), VersionSupporterImpl.EMPTY.getClassFileLocator())
                                    .on(isAnnotatedWith(Monitor.class)));
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
        return resettable;
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
        OutputClassFileContent.output(typeDescription.getName(), dynamicType.getBytes());
    }
}

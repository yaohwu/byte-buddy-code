package xyz.yaohwu.dynamic;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.LoadedTypeInitializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

public enum VersionSupporterImpl implements VersionSupporter {

    ACTUAL {
        private boolean init = false;
        private TypeDescription advisorTypeDesc = null;
        private ClassFileLocator fileLocator = null;

        @Override
        public TypeDescription getAdvisorTypeDesc() {
            if (!init) {
                init();
            }
            return advisorTypeDesc;
        }

        @Override
        public ClassFileLocator getClassFileLocator() {
            if (!init) {
                init();
            }
            return fileLocator;
        }

        private void init() {
            ClassLoader classLoader = getClassLoader();
            DynamicType.Unloaded<?> advisorType = new ByteBuddy()
                    .redefine(CallAdvisor.class)
                    .name("xyz.yaohwu.dynamic.ActualCallAdvisor")
                    .make();

            fileLocator =
                    new ClassFileLocator.Compound(
                            ClassFileLocator.Simple.of("xyz.yaohwu.dynamic.ActualCallAdvisor", advisorType.getBytes()),
                            ClassFileLocator.ForClassLoader.of(classLoader)
                    );
            VersionSupporterImpl.outputClassContentToFile("xyz.yaohwu.dynamic.ActualCallAdvisor", advisorType.getBytes());
            advisorTypeDesc = advisorType.getTypeDescription();
            if (advisorTypeDesc == null || fileLocator == null) {
                System.out.println("try using advisor version failed");
            }
            init = true;
        }
    },
    EMPTY {
        private boolean init = false;
        private TypeDescription advisorTypeDesc = null;
        private ClassFileLocator fileLocator = null;

        @Override
        public TypeDescription getAdvisorTypeDesc() {
            if (!init) {
                init();
            }
            return advisorTypeDesc;
        }

        @Override
        public ClassFileLocator getClassFileLocator() {
            if (!init) {
                init();
            }
            return fileLocator;
        }

        private void init() {
            ClassLoader classLoader = getClassLoader();
            DynamicType.Unloaded<?> advisorType = new ByteBuddy()
                    .redefine(LocalParaCallAdvisor.class)
                    .name("xyz.yaohwu.dynamic.ActualEmptyAdvisor")
                    // make something
                    .initializer(new LoadedTypeInitializer.ForStaticField("a", "cool"))
                    .make();

            // make class loaded by the app class loader
            advisorType.load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.UsingLookup.withFallback(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return MethodHandles.lookup();
                }
            }));


            fileLocator =
                    new ClassFileLocator.Compound(
                            ClassFileLocator.Simple.of("xyz.yaohwu.dynamic.ActualEmptyAdvisor", advisorType.getBytes()),
                            ClassFileLocator.ForClassLoader.of(classLoader)
                    );
            VersionSupporterImpl.outputClassContentToFile("xyz.yaohwu.dynamic.ActualEmptyAdvisor", advisorType.getBytes());
            advisorTypeDesc = advisorType.getTypeDescription();
            if (advisorTypeDesc == null || fileLocator == null) {
                System.out.println("try using empty advisor version failed");
            }
            init = true;
        }
    };

    protected ClassLoader getClassLoader() {
        ClassLoader classLoader;
        classLoader = this.getClass().getClassLoader().getParent();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    private static void outputClassContentToFile(String name, byte[] bytes) {
        File file = new File("/Users/yaohwu/finecode/recompile/recompile-code/" + name + "." + "advisor" + ".class");
        try {
            if ((file.exists() && file.delete() && file.createNewFile()) || (!file.exists() && file.createNewFile())) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
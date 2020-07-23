package xyz.yaohwu.loader;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import org.joor.Reflect;

import java.lang.reflect.Modifier;
import java.net.URL;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/23 10:32
 */
public class MainForLoadNewClass {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        ClassLoader definedClassLoader = new MyClassLoader(new URL[0]);

        DynamicType.Unloaded<?> newType = new ByteBuddy()
                .subclass(Object.class)
                .name("xyz.yaohwu.defined.loader.NewObject")
                .defineMethod("say", String.class, Modifier.PUBLIC).intercept(FixedValue.value("hello!"))
                .make();
        DynamicType.Loaded<?> loadedType = newType.load(definedClassLoader);

        Class<?> clazz = loadedType.getLoaded();

        ClassLoader newDefinedClassLoader = clazz.getClassLoader();
        System.out.println(
                "Classloader relationship is that \"The parent of newDefinedClassLoader is definedClassLoader.\" "
                        + (definedClassLoader == newDefinedClassLoader.getParent() ? "✅" : "❌"));
        System.out.println(Reflect.on(clazz.newInstance()).call("say"));
    }
}

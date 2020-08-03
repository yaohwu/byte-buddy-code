package xyz.yaohwu.dynamic;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/24 22:38
 */
public interface VersionSupporter {

    TypeDescription getAdvisorTypeDesc();

    ClassFileLocator getClassFileLocator();

}

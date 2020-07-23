package xyz.yaohwu.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/23 10:32
 */
public class MyClassLoader extends URLClassLoader {

    public MyClassLoader(URL[] urls) {
        super(urls);
    }
}

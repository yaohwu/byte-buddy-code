package xyz.yaohwu.tool;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yaohwu
 * created by yaohwu at 2020/8/3 15:27
 */
public final class OutputContent {

    private OutputContent() {
    }

    public static void output(String simpleName, byte[] content) {
        File file = new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code/" + simpleName + ".class");
        try {
            if ((file.exists() && file.delete() && file.createNewFile()) || (!file.exists() && file.createNewFile())) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(content);
                    fileOutputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void output(TypeDescription typeDescription, DynamicType dynamicType, String name) {
        File file = new File("/Users/yaohwu/mycode/byte-buddy-demo/recompile-code/" + typeDescription.getName() + "." + name + ".class");
        try {
            if ((file.exists() && file.delete() && file.createNewFile()) || (!file.exists() && file.createNewFile())) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(dynamicType.getBytes());
                    fileOutputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

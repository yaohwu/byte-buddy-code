package xyz.yaohwu.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yaohwu
 * created by yaohwu at 2020/8/3 15:27
 */
public final class OutputClassFileContent {

    private OutputClassFileContent() {
    }

    public static void output(String simpleName, byte[] content) {
        File file = new File("/Users/yaohwu/finecode/recompile/recompile-code/" + simpleName + ".class");
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
}

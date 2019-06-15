package com.cloud.task.util;

import java.io.*;
import java.nio.file.Files;

/**
 * 〈java文件处理工具类〉<br> 
 *
 * @author number68
 * @date 2019/6/10
 * @since 0.1
 */
public class JavaFileUtil {
    public static void main(String[] args) {
        File file = new File("E:/code/temp/scheduled-task/src/main/java");
        System.out.println(file.getAbsolutePath());
        dealFile(file);
    }

    private static void dealFile(File file) {
        if (!file.exists()) {
            return;
        }

        File[] childFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                } else if (pathname.getName().endsWith(".java")) {
                    deleteFileComment(pathname);
                    return false;
                }
                return false;
            }
        });

        for (File childFile : childFiles) {
            dealFile(childFile);
        }
    }

    private static void deleteFileComment(File file) {
        String copyName = file.getName().replace(".", "_old.");
        File copyFile = new File(file.getParent() + "/" + copyName);
        System.out.println(copyFile.getAbsolutePath());
        try {
            if (copyFile.exists()) {
                copyFile.delete();
            }
            copyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(copyFile))) {

            String readLine;
            boolean canWriteNow = false;
            while ((readLine = br.readLine()) != null) {
                if (!canWriteNow && readLine.startsWith("package")) {
                    canWriteNow = true;
                }

                if (canWriteNow) {
                    bw.write(readLine);
                    bw.write("\r\n");
                }

            }
            bw.flush();
            file.setExecutable(true);
            br.close();
            Files.delete(file.toPath());
            bw.close();
            copyFile.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package me.itzsomebody.radon.utils;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
    /**
     * Renames an existing file to EXISTING-FILE.jar.BACKUP-X.
     *
     * @param existing existing file to rename.
     * @return the new name of the existing name.
     */
    public static String renameExistingFile(File existing) {
        int i = 0;

        while (true) {
            i++;
            String newName = existing.getAbsolutePath() + ".BACKUP-" + String.valueOf(i);
            File backUpName = new File(newName);
            if (!backUpName.exists()) {
                existing.renameTo(backUpName);
                existing.delete();
                return newName;
            }
        }

    }

    /**
     * Searches sub directories for libraries
     *
     * @param file      should be directory
     * @param libraries all libraries collected.
     * @author Richard Xing
     */
    public static void getSubDirectoryFiles(File file, List<File> libraries) {
        if (!file.isFile() && file.listFiles() != null) {
            Stream.of(file.listFiles()).forEach(f -> {
                // 输出元素名称

                if (f.isDirectory()) {
                    getSubDirectoryFiles(f, libraries);
                } else {
                    if (f.getName().toLowerCase().endsWith(".jar")) {
                        //System.out.println(fileLists[i].getName());
                        libraries.add(f);
                    }
                }
            });
        }
    }
}

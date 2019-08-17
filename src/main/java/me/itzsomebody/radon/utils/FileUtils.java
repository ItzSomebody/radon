/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
                        //System.out.println(fileLists[i].getConfigName());
                        libraries.add(f);
                    }
                }
            });
        }
    }
}

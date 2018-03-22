package me.itzsomebody.radon.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utils for operating on files in general.
 *
 * @author ItzSomebody
 * @author c001man
 */
public class FileUtils {
    /**
     * Renames an existing file to EXISTINGFILE.jar.BACKUP-X.
     *
     * @param existing existing file to rename.
     * @return the new name of the existing name.
     */
    public static String renameExistingFile(File existing) {
        int i = 0;

        while (true) {
            i++;
            String newName = existing.getAbsolutePath() + ".BACKUP-"
                    + String.valueOf(i);
            File backUpName = new File(newName);
            if (!backUpName.exists()) {
                existing.renameTo(backUpName);
                existing.delete();
                return newName;
            }
        }

    }

    /**
     * Writes a bytes to a {@link ZipOutputStream}.
     *
     * @param zos the {@link ZipOutputStream} to write to.
     * @param data bytes to write to output
     * @throws IOException if an error happens while writing to output stream.
     */
    public static void writeToZip(ZipOutputStream zos, byte[] data)
            throws IOException {
        zos.write(data);
        zos.closeEntry();
    }
}

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
     * Writes an {@link InputStream} to a {@link ZipOutputStream}.
     *
     * @param zos the {@link ZipOutputStream} to write the input
     * {@link InputStream} to.
     * @param in  the {@link InputStream} to write to the output
     * {@link ZipOutputStream}.
     * @throws IOException if an error happens while writing to output stream.
     */
    public static void writeToZip(ZipOutputStream zos, InputStream in)
            throws IOException {
        byte[] buffer = new byte[1024];
        try {
            while (in.available() > 0) {
                int data = in.read(buffer);
                zos.write(buffer, 0, data);
            }
        } finally {
            in.close();
            zos.closeEntry();
        }
    }
}

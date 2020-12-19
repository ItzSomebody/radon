/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.utils;

import xyz.itzsomebody.radon.exceptions.PreventableRadonException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for various I/O and File operations.
 *
 * @author itzsomebody
 */
public class IOUtils {
    /**
     * Return a given {@link InputStream} as a byte array.
     *
     * @return Byte array representation of provided {@link InputStream}.
     */
    public static byte[] toByteArray(final InputStream stream) throws IOException {
        try (stream; var out = new ByteArrayOutputStream()) {
            var buf = new byte[0x1000];
            int bytesRead;
            while ((bytesRead = stream.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            out.flush();
            return out.toByteArray();
        }
    }

    /**
     * Renames a {@link File} and returns the new name.
     *
     * @return The new {@link File} name.
     */
    public static String renameExistingFile(File existing) {
        try {
            var i = 0;
            while (true) {
                i++;
                var newName = existing.getAbsolutePath() + ".BACKUP-" + i;
                var backUpName = new File(newName);
                if (!backUpName.exists()) {
                    existing.renameTo(backUpName);
                    return newName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new PreventableRadonException(String.format("Could not backup \"%s\"", existing.getAbsolutePath()));
        }
    }
}

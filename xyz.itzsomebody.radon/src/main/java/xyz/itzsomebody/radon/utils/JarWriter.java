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

import xyz.itzsomebody.radon.Radon;
import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.config.ObfConfig;
import xyz.itzsomebody.radon.exceptions.FatalRadonException;
import xyz.itzsomebody.radon.exceptions.PreventableRadonException;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarWriter {
    private int fakeIndex;

    public void write(String path) {
        var radon = Radon.getInstance();

        var level = radon.<Integer>getConfigValue(ObfConfig.Key.COMPRESSION_LEVEL.getKeyString());
        var store = radon.<Boolean>getConfigValue(ObfConfig.Key.USE_STORE.getKeyString());
        var corruptCrcs = radon.<Boolean>getConfigValue(ObfConfig.Key.CORRUPT_CRCS.getKeyString());
        var antiExtraction = radon.<Boolean>getConfigValue(ObfConfig.Key.ANTI_EXTRACTION.getKeyString());
        var fakeEntries = radon.<Integer>getConfigValue(ObfConfig.Key.FAKE_DUPLICATE_ENTRIES.getKeyString());

        if (corruptCrcs && store) {
            // fixme: maybe check this when loading config so people don't get annoyed that this wasn't checked until jar write
            throw new PreventableRadonException("Cannot use store and corrupt_crcs together");
        }

        var classes = radon.getClasses();
        var resources = radon.getResources();

        RadonLogger.info(String.format("Writing output to \"%s\"", path));
        var file = new File(path);

        if (file.exists()) {
            RadonLogger.info(String.format("Output already exists! Backed up to \"%s\"", IOUtils.renameExistingFile(file)));
        }

        try {
            var stream = new ZipOutputStream(new FileOutputStream(file));

            stream.setLevel(level);
            if (fakeEntries > 0) {
                disableNameCache(stream);
            }
            if (store) {
                stream.setMethod(ZipOutputStream.STORED);
                stream.setLevel(Deflater.NO_COMPRESSION);
            }
            if (corruptCrcs) {
                injectCrcCorrupter(stream);
            }

            classes.forEach(((name, wrapper) -> {
                try {
                    // The fact that this incredibly stupid but simple anti-extraction trick works simply blows my mind
                    var entry = new ZipEntry(wrapper.getName() + ".class" + (antiExtraction ? "/" : ""));
                    var data = wrapper.toByteArray();
                    writeEntry(stream, entry, data, store);
                    if (antiExtraction) {
                        writeAntiExtractionEntries(stream, entry.getName(), store);
                    }

                    if (fakeEntries > 0) {
                        for (int i = 0; i < fakeEntries; i++) {
                            var fakeEntry = new ZipEntry(entry.getName());
                            var fakeData = RandomUtils.randomBytes();
                            writeEntry(stream, fakeEntry, fakeData, store);

                            if (antiExtraction) {
                                writeAntiExtractionEntries(stream, fakeEntry.getName(), store);
                            }
                        }
                    }
                } catch (Throwable t) {
                    RadonLogger.warn(String.format("An error happened while writing class \"%s\"", name));

                    if (RadonConstants.VERBOSE) {
                        t.printStackTrace();
                    }
                }
            }));

            resources.forEach((name, data) -> {
                try {
                    writeEntry(stream, new ZipEntry(name), data, store);
                } catch (IOException ioe) {
                    RadonLogger.warn(String.format("An IO error happened while writing resource \"%s\"", name));

                    if (RadonConstants.VERBOSE) {
                        ioe.printStackTrace();
                    }
                }
            });

            stream.setComment(radon.getConfigValue(ObfConfig.Key.ZIP_COMMENT.getKeyString()));
            stream.close();
        } catch (IOException ioe) {
            if (RadonConstants.VERBOSE) {
                ioe.printStackTrace();
            }

            throw new FatalRadonException("An IO error happened while writing output: " + ioe.getMessage());
        }
    }

    private void writeAntiExtractionEntries(ZipOutputStream stream, String name, boolean store) throws IOException {
        // index entry
        var localIndex = fakeIndex++;
        var indexBytes = new byte[]{ // Completely unnecessary, but fun so why not lol
                (byte) localIndex,
                (byte) (localIndex >>> 8),
                (byte) (localIndex >>> 16),
                (byte) (localIndex >>> 24)
        };
        writeEntry(stream, new ZipEntry(name + "index"), indexBytes, store);

        // name entry
        writeEntry(stream, new ZipEntry(name + "name"), name.substring(0, name.length() - 1).getBytes(StandardCharsets.UTF_8), store);

        // data_offset entry
        var randomInt = RandomUtils.randomInt();
        var randomIntBytes = new byte[]{ // lmao, here we go again
                (byte) randomInt,
                (byte) (randomInt >>> 8),
                (byte) (randomInt >>> 16),
                (byte) (randomInt >>> 24)
        };
        writeEntry(stream, new ZipEntry(name + "data_offset"), randomIntBytes, store);
    }

    private void writeEntry(ZipOutputStream stream, ZipEntry entry, byte[] data, boolean store) throws IOException {
        if (store) {
            populateStoredEntryInfo(entry, data);
        }

        stream.putNextEntry(entry);
        stream.write(data);
        stream.closeEntry();
    }

    private void disableNameCache(ZipOutputStream stream) {
        // For reasons beyond my comprehension, this is 100% legal to do
        try {
            var clazz = ZipOutputStream.class;
            var field = clazz.getDeclaredField("names");
            field.setAccessible(true);
            field.set(stream, new HashSet<>(0) {
                @Override
                public boolean add(Object o) {
                    // lol git rekt
                    return true;
                }
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new FatalRadonException("Error while attempting to disable ZipOutputString name cache: " + e.toString());
        }
    }

    private void injectCrcCorrupter(ZipOutputStream stream) {
        // tl;dr: "oh man the zip format is really f***ed"
        // ~samczsun
        try {
            var clazz = ZipOutputStream.class;
            var field = clazz.getDeclaredField("crc");
            field.setAccessible(true);
            field.set(stream, new CRC32() {
                @Override
                public void update(byte[] b, int off, int len) {
                    // Don't update the CRC
                }

                @Override
                public long getValue() {
                    return RandomUtils.randomLong(0xFFFFFFFFL);
                }
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new FatalRadonException("Error while attempting to inject CRC corrupter: " + e.toString());
        }
    }

    private void populateStoredEntryInfo(ZipEntry entry, byte[] data) {
        var crc = new CRC32();
        crc.update(data);

        entry.setCrc(crc.getValue());
        entry.setSize(data.length);
        entry.setCompressedSize(data.length);
    }
}

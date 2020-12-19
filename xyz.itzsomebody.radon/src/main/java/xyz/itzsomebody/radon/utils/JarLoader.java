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

import org.objectweb.asm.ClassReader;
import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.exceptions.FatalRadonException;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class JarLoader {
    private final Map<String, ClassWrapper> classes = new HashMap<>();
    private final Map<String, ClassWrapper> classpath = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();

    public void loadAsLib(String path) {
        RadonLogger.info("Loading library \"" + path + "\"");
        var file = new File(path);

        if (!file.exists()) {
            RadonLogger.warn("Library \"" + file.getAbsolutePath() + "\" doesn't exist. Skipping");
        }
        if (!file.canRead()) {
            RadonLogger.warn("Library \"" + file.getAbsolutePath() + "\" doesn't have read permissions. Skipping");
        }
        if (!file.isFile()) {
            RadonLogger.warn("Library \"" + file.getAbsolutePath() + "\" isn't a file. Skipping");
        }

        try {
            var zipFile = new ZipFile(file);
            var entries = zipFile.entries();

            ZipEntry currentEntry;
            while (entries.hasMoreElements()) {
                currentEntry = entries.nextElement();

                if (!currentEntry.isDirectory() && currentEntry.getName().endsWith(".class")) {
                    try (var stream = zipFile.getInputStream(currentEntry)) {
                        var wrapper = ClassWrapper.fromLib(new ClassReader(stream));
                        classpath.put(wrapper.getName(), wrapper);
                    } catch (Throwable t) {
                        RadonLogger.warn(String.format("Error while loading library class: \"%s\"", currentEntry.getName()));

                        if (RadonConstants.VERBOSE) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        } catch (ZipException e) {
            RadonLogger.warn("Library \"" + file.getAbsolutePath() + "\" couldn't be loaded as a ZIP. Skipping");

            if (RadonConstants.VERBOSE) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            RadonLogger.warn("Library \"" + file.getAbsolutePath() + "\" couldn't be loaded due to an IO error" + e.getMessage());

            if (RadonConstants.VERBOSE) {
                e.printStackTrace();
            }
        }
    }

    public void loadAsInput(String path) {
        RadonLogger.info("Loading input \"" + path + "\"");
        var file = new File(path);

        if (!file.exists()) {
            throw new FatalRadonException("Input \"" + file.getAbsolutePath() + "\" doesn't exist");
        }
        if (!file.canRead()) {
            throw new FatalRadonException("Input \"" + file.getAbsolutePath() + "\" doesn't have read permissions");
        }
        if (!file.isFile()) {
            throw new FatalRadonException("Input \"" + file.getAbsolutePath() + "\" isn't a file");
        }

        try {
            var zipFile = new ZipFile(file);
            var entries = zipFile.entries();

            ZipEntry currentEntry;
            while (entries.hasMoreElements()) {
                currentEntry = entries.nextElement();

                if (!currentEntry.isDirectory()) {
                    try (var stream = zipFile.getInputStream(currentEntry)) {
                        if (currentEntry.getName().endsWith(".class")) {
                            try {
                                var wrapper = ClassWrapper.from(new ClassReader(stream));
                                classes.put(wrapper.getName(), wrapper);
                                classpath.put(wrapper.getName(), wrapper);
                            } catch (Throwable t) {
                                RadonLogger.warn(String.format("Error while loading input class: \"%s\" (loading as resources instead)", currentEntry.getName()));
                                resources.put(currentEntry.getName(), IOUtils.toByteArray(stream));
                            }
                        } else {
                            resources.put(currentEntry.getName(), IOUtils.toByteArray(stream));
                        }
                    }
                }
            }
        } catch (ZipException e) {
            throw new FatalRadonException("Input \"" + file.getAbsolutePath() + "\" couldn't be loaded as a ZIP. (" + e.getMessage() + ")");
        } catch (IOException e) {
            throw new FatalRadonException("Input \"" + file.getAbsolutePath() + "\" couldn't be due to an IO error. (" + e.getMessage() + ")");
        }
    }

    public Map<String, ClassWrapper> getClasses() {
        return classes;
    }

    public Map<String, ClassWrapper> getClasspath() {
        return classpath;
    }

    public Map<String, byte[]> getResources() {
        return resources;
    }
}

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

package me.itzsomebody.radon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import me.itzsomebody.radon.asm.ClassTree;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.exceptions.MissingClassException;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.miscellaneous.TrashClasses;
import me.itzsomebody.radon.utils.FileUtils;
import me.itzsomebody.radon.utils.IOUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.MethodNode;

/**
 * This class is how Radon processes the provided {@link ObfuscationConfiguration} to produce an obfuscated jar.
 *
 * @author ItzSomebody
 */
public class Radon {
    private ObfuscationConfiguration config;
    private Map<String, ClassTree> hierarchy = new HashMap<>();
    public Map<String, ClassWrapper> classes = new HashMap<>();
    public Map<String, ClassWrapper> classPath = new HashMap<>();
    public Map<String, byte[]> resources = new HashMap<>();

    public Radon(ObfuscationConfiguration config) {
        this.config = config;
    }

    /**
     * Execution order. Feel free to modify.
     */
    public void run() {
        loadClassPath();
        loadInput();

        List<Transformer> transformers = getConfig().getTransformers();

        if (getConfig().getnTrashClasses() > 0)
            transformers.add(new TrashClasses());
        if (transformers.isEmpty())
            throw new RadonException("No transformers are enabled.");

        transformers.sort((t1, t2) -> {
            ExclusionType type1 = t1.getExclusionType();
            ExclusionType type2 = t2.getExclusionType();

            // In the event I forget to add an exclusion type
            if (type1 == null)
                throw new RadonException(t1.getName() + " has a null exclusion type");
            if (type2 == null)
                throw new RadonException(t2.getName() + " has a null exclusion type");

            return Integer.compare(type1.ordinal(), type2.ordinal());
        });

        Main.info("------------------------------------------------");
        transformers.stream().filter(Objects::nonNull).forEach(transformer -> {
            long current = System.currentTimeMillis();
            Main.info(String.format("Running %s transformer.", transformer.getName()));
            transformer.init(this);
            transformer.transform();
            Main.info(String.format("Finished running %s transformer. [%dms]", transformer.getName(), (System.currentTimeMillis() - current)));
            Main.info("------------------------------------------------");
        });

        writeOutput();
    }

    private void writeOutput() {
        File output = config.getOutput();
        Main.info(String.format("Writing output to \"%s\".", output.getAbsolutePath()));

        if (output.exists())
            Main.info(String.format("Output file already exists, renamed to %s.", FileUtils.renameExistingFile(output)));

        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
            zos.setLevel(getConfig().getCompressionLevel());

            if (config.isCorruptCrc())
                try {
                    Field field = ZipOutputStream.class.getDeclaredField("crc");
                    field.setAccessible(true);
                    field.set(zos, new CRC32() {
                        @Override
                        public void update(byte[] b, int off, int len) {
                            // Don't update the CRC
                        }

                        @Override
                        public long getValue() {
                            return RandomUtils.getRandomLong(0xFFFFFFFFL);
                        }
                    });

                    Main.info("Injected CRC corrupter.");
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.severe("Failed to inject CRC field.");
                }


            classes.values().forEach(classWrapper -> {
                try {
                    ZipEntry entry = new ZipEntry(classWrapper.getEntryName());

                    zos.putNextEntry(entry);
                    zos.write(classWrapper.toByteArray(this));
                    zos.closeEntry();
                } catch (Throwable t) {
                    Main.severe(String.format("Error writing class %s. Skipping.", classWrapper.getName() + ".class"));
                    t.printStackTrace();
                }
            });

            resources.forEach((name, bytes) -> {
                try {
                    ZipEntry entry = new ZipEntry(name);

                    zos.putNextEntry(entry);
                    zos.write(bytes);
                    zos.closeEntry();
                } catch (IOException ioe) {
                    Main.severe(String.format("Error writing resource %s. Skipping.", name));
                    ioe.printStackTrace();
                }
            });

            zos.setComment(Main.ATTRIBUTION);
            zos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RadonException();
        }
    }

    private void loadClassPath() {
        getConfig().getLibraries().forEach(file -> {
            if (file.exists()) {
                Main.info(String.format("Loading library \"%s\".", file.getAbsolutePath()));

                try {
                    ZipFile zipFile = new ZipFile(file);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();

                        if (!entry.isDirectory() && entry.getName().endsWith(".class"))
                            try {
                                ClassWrapper cw = new ClassWrapper(new ClassReader(zipFile.getInputStream(entry)), true);
                                classPath.put(cw.getName(), cw);
                            } catch (Throwable t) {
                                Main.severe(String.format("Error while loading library class \"%s\".", entry.getName().replace(".class", "")));
                                t.printStackTrace();
                            }
                    }
                } catch (ZipException e) {
                    Main.severe(String.format("Library \"%s\" could not be opened as a zip file.", file.getAbsolutePath()));
                    e.printStackTrace();
                } catch (IOException e) {
                    Main.severe(String.format("IOException happened while trying to load classes from \"%s\".", file.getAbsolutePath()));
                    e.printStackTrace();
                }
            } else
                Main.warning(String.format("Library \"%s\" could not be found and will be ignored.", file.getAbsolutePath()));
        });
    }

    private void loadInput() {
        File input = this.getConfig().getInput();

        if (input.exists()) {
            Main.info(String.format("Loading input \"%s\".", input.getAbsolutePath()));

            try {
                ZipFile zipFile = new ZipFile(input);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    InputStream in = zipFile.getInputStream(entry);

                    if (!entry.isDirectory())
                        if (entry.getName().endsWith(".class"))
                            try {
                                ClassWrapper cw = new ClassWrapper(new ClassReader(in), false);

                                if (cw.getVersion() <= Opcodes.V1_5)
                                    for (int i = 0; i < cw.getMethods().size(); i++) {
                                        MethodNode methodNode = cw.getMethods().get(i).getMethodNode();
                                        JSRInlinerAdapter adapter = new JSRInlinerAdapter(methodNode, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[0]));
                                        methodNode.accept(adapter);
                                        cw.getMethods().get(i).setMethodNode(adapter);
                                    }

                                classPath.put(cw.getName(), cw);
                                classes.put(cw.getName(), cw);

                                String entryName = entry.getName();
                                String wrapperEntryName = cw.getEntryName();
                                if (entryName.endsWith(wrapperEntryName) && !entryName.equals(wrapperEntryName))
                                    cw.setEntryPrefix(entry.getName().substring(0, entryName.length() - wrapperEntryName.length()));
                            } catch (Throwable t) {
                                Main.warning(String.format("Could not load %s as a class.", entry.getName()));
                                this.resources.put(entry.getName(), IOUtils.toByteArray(in));
                            }
                        else
                            this.resources.put(entry.getName(), IOUtils.toByteArray(in));
                }
            } catch (ZipException e) {
                Main.severe(String.format("Input file \"%s\" could not be opened as a zip file.", input.getAbsolutePath()));
                e.printStackTrace();
                throw new RadonException(e);
            } catch (IOException e) {
                Main.severe(String.format("IOException happened while trying to load classes from \"%s\".", input.getAbsolutePath()));
                e.printStackTrace();
                throw new RadonException(e);
            }
        } else {
            Main.severe(String.format("Unable to find \"%s\".", input.getAbsolutePath()));
            throw new RadonException();
        }
    }

    /**
     * Finds {@link ClassWrapper} with given name.
     *
     * @return {@link ClassWrapper}.
     * @throws RadonException if not found.
     */
    public ClassWrapper getClassWrapper(String ref) {
        if (!classPath.containsKey(ref))
            throw new RadonException("Could not find " + ref);

        return classPath.get(ref);
    }

    /**
     * Finds {@link ClassTree} with given name.
     *
     * @return {@link ClassTree}.
     * @throws RadonException if there are missing classes needed to build the inheritance tree.
     */
    public ClassTree getTree(String ref) {
        if (!hierarchy.containsKey(ref)) {
            ClassWrapper wrapper = getClassWrapper(ref);
            buildHierarchy(wrapper, null);
        }

        return hierarchy.get(ref);
    }

    private void buildHierarchy(ClassWrapper wrapper, ClassWrapper sub) {
        if (hierarchy.get(wrapper.getName()) == null) {
            ClassTree tree = new ClassTree(wrapper);

            if (wrapper.getSuperName() != null) {
                tree.getParentClasses().add(wrapper.getSuperName());

                buildHierarchy(getClassWrapper(wrapper.getSuperName()), wrapper);
            }
            if (wrapper.getInterfaces() != null)
                wrapper.getInterfaces().forEach(s -> {
                    tree.getParentClasses().add(s);

                    buildHierarchy(getClassWrapper(s), wrapper);
                });

            hierarchy.put(wrapper.getName(), tree);
        }

        if (sub != null)
            hierarchy.get(wrapper.getName()).getSubClasses().add(sub.getName());
    }

    public void buildInheritance() {
        classes.values().forEach(classWrapper -> buildHierarchy(classWrapper, null));
    }

    /**
     * Equivalent to the following:
     * Class clazz1 = something;
     * Class class2 = somethingElse;
     * return class1.isAssignableFrom(class2);
     */
    public boolean isAssignableFrom(String type1, String type2) {
        if ("java/lang/Object".equals(type1))
            return true;
        if (type1.equals(type2))
            return true;

        getClassWrapper(type1);
        getClassWrapper(type2);

        ClassTree firstTree = getTree(type1);
        if (firstTree == null)
            throw new MissingClassException("Could not find " + type1 + " in the built class hierarchy");

        Set<String> allChildren = new HashSet<>();
        Deque<String> toProcess = new ArrayDeque<>(firstTree.getSubClasses());
        while (!toProcess.isEmpty()) {
            String s = toProcess.poll();

            if (allChildren.add(s)) {
                getClassWrapper(s);
                ClassTree tempTree = getTree(s);
                toProcess.addAll(tempTree.getSubClasses());
            }
        }
        return allChildren.contains(type2);
    }

    public ObfuscationConfiguration getConfig() {
        return config;
    }
}

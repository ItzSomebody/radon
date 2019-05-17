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

package me.itzsomebody.radon.transformers.obfuscators.renamer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassTree;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.asm.MemberRemapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.FileUtils;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;


/**
 * Transformer which renames classes and their members.
 *
 * @author ItzSomebody
 */
public class Renamer extends Transformer {
    private static final Map<String, RenamerSetting> KEY_MAP = new HashMap<>();
    private String[] adaptTheseResources;
    private boolean dumpMappings;
    private String repackageName;
    private Map<String, String> mappings;

    static {
        Stream.of(RenamerSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    private static boolean methodCanBeRenamed(MethodWrapper wrapper) {
        return !wrapper.getAccess().isNative() && !"main".equals(wrapper.getOriginalName())
                && !"premain".equals(wrapper.getOriginalName()) && !wrapper.getOriginalName().startsWith("<");
    }

    @Override
    public void transform() {
        mappings = new HashMap<>();
        Map<String, String> packageMappings = new HashMap<>();

        Main.info("Generating mappings.");
        long current = System.currentTimeMillis();

        getClassWrappers().forEach(classWrapper -> {
            classWrapper.getMethods().stream().filter(Renamer::methodCanBeRenamed).forEach(methodWrapper -> {
                HashSet<String> visited = new HashSet<>();

                if (!cannotRenameMethod(radon.getTree(classWrapper.getOriginalName()), methodWrapper, visited))
                    genMethodMappings(methodWrapper, methodWrapper.getOwner().getOriginalName(), uniqueRandomString());
            });

            classWrapper.getFields().forEach(fieldWrapper -> {
                HashSet<String> visited = new HashSet<>();

                if (!cannotRenameField(radon.getTree(classWrapper.getOriginalName()), fieldWrapper, visited))
                    genFieldMappings(fieldWrapper, fieldWrapper.getOwner().getOriginalName(), uniqueRandomString());
            });

            if (!excluded(classWrapper)) {
                String newName;

                if (getRepackageName() == null) {
                    String mappedPackageName = randomString();

                    packageMappings.putIfAbsent(classWrapper.getPackageName(), mappedPackageName);
                    newName = packageMappings.get(classWrapper.getPackageName());
                } else
                    newName = getRepackageName();

                if (!newName.isEmpty())
                    newName += '/' + uniqueRandomString();
                else
                    newName = uniqueRandomString();

                mappings.put(classWrapper.getOriginalName(), newName);
            }
        });

        Main.info(String.format("Finished generated mappings. [%dms]", tookThisLong(current)));
        Main.info("Applying mappings.");
        current = System.currentTimeMillis();

        // Apply mappings
        Remapper simpleRemapper = new MemberRemapper(mappings);
        new ArrayList<>(getClassWrappers()).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.getClassNode();

            ClassNode copy = new ClassNode();
            classNode.accept(new ClassRemapper(copy, simpleRemapper));

            // In order to preserve the original names to prevent exclusions from breaking,
            // we update the MethodNode/FieldNode/ClassNode each wrapper wraps instead.
            IntStream.range(0, copy.methods.size())
                    .forEach(i -> classWrapper.getMethods().get(i).setMethodNode(copy.methods.get(i)));

            if (copy.fields != null)
                IntStream.range(0, copy.fields.size())
                        .forEach(i -> classWrapper.getFields().get(i).setFieldNode(copy.fields.get(i)));

            classWrapper.setClassNode(copy);

            getClasses().remove(classWrapper.getOriginalName());
            getClasses().put(classWrapper.getName(), classWrapper);
            getClassPath().put(classWrapper.getName(), classWrapper);
        });

        Main.info(String.format("Mapped %d members. [%dms]", mappings.size(), tookThisLong(current)));
        current = System.currentTimeMillis();

        // Now we gotta fix those resources because we probably screwed up random files.
        Main.info("Attempting to map class names in resources");
        AtomicInteger fixed = new AtomicInteger();
        getResources().forEach((name, byteArray) -> {
            if (getAdaptTheseResources() != null)
                Stream.of(getAdaptTheseResources()).forEach(s -> {
                    Pattern pattern = Pattern.compile(s);

                    if (pattern.matcher(name).matches()) {
                        String stringVer = new String(byteArray, StandardCharsets.UTF_8);

                        for (String mapping : mappings.keySet()) {
                            String original = mapping.replace("/", ".");
                            if (stringVer.contains(original)) {
                                // Regex that ensures that class names that match words in the manifest don't break the
                                // manifest.
                                // Example: name == Main
                                if ("META-INF/MANIFEST.MF".equals(name) // Manifest
                                        || "plugin.yml".equals(name) // Spigot plugin
                                        || "bungee.yml".equals(name)) // Bungeecord plugin
                                    stringVer = stringVer.replaceAll("(?<=[: ])" + original,
                                            mappings.get(mapping).replace("/", "."));
                                else
                                    stringVer = stringVer.replace(original, mappings.get(mapping).replace("/", "."));
                            }
                        }

                        getResources().put(name, stringVer.getBytes(StandardCharsets.UTF_8));
                        fixed.incrementAndGet();
                    }
                });
        });

        Main.info(String.format("Mapped %d names in resources. [%dms]", fixed.get(), tookThisLong(current)));

        if (isDumpMappings())
            dumpMappings();
    }

    private void genMethodMappings(MethodWrapper methodWrapper, String owner, String newName) {
        String key = owner + '.' + methodWrapper.getOriginalName() + methodWrapper.getOriginalDescription();

        // This (supposedly) will always stop the recursion because the tree was already renamed
        if (mappings.containsKey(key))
            return;

        ClassTree tree = radon.getTree(owner);

        mappings.put(key, newName);

        if (!Modifier.isStatic(methodWrapper.getMethodNode().access)) { // Static methods can't be overridden
            tree.getParentClasses().forEach(parentClass -> genMethodMappings(methodWrapper, parentClass, newName));
            tree.getSubClasses().forEach(subClass -> genMethodMappings(methodWrapper, subClass, newName));
        }
    }

    private boolean cannotRenameMethod(ClassTree tree, MethodWrapper wrapper, Set<String> visited) {
        String check = tree.getClassWrapper().getOriginalName() + '.' + wrapper.getOriginalName() + wrapper.getOriginalDescription();

        // Don't check these
        if (visited.contains(check))
            return false;

        visited.add(check);

        // If excluded, we don't want to rename.
        // If we already mapped the tree, we don't want to waste time doing it again.
        if (excluded(check) || mappings.containsKey(check))
            return true;

        // Methods which are static don't need to be checked for inheritance
        if (!Modifier.isStatic(wrapper.getMethodNode().access)) {
            // We can't rename members which inherit methods from external libraries
            if (tree.getClassWrapper() != wrapper.getOwner() && tree.getClassWrapper().isLibraryNode()
                    && tree.getClassWrapper().getMethods().stream().anyMatch(mw -> mw.getOriginalName().equals(wrapper.getOriginalName())
                    && mw.getOriginalDescription().equals(wrapper.getOriginalDescription())))
                return true;

            return tree.getParentClasses().stream().anyMatch(parent -> cannotRenameMethod(radon.getTree(parent), wrapper, visited))
                    || (tree.getSubClasses().stream().anyMatch(sub -> cannotRenameMethod(radon.getTree(sub), wrapper, visited)));
        }

        return false;
    }

    private void genFieldMappings(FieldWrapper fieldWrapper, String owner, String newName) {
        // This (supposedly) will always stop the recursion because the tree was already renamed
        if (mappings.containsKey(owner + '.' + fieldWrapper.getOriginalName() + '.' + fieldWrapper.getOriginalDescription()))
            return;

        ClassTree tree = radon.getTree(owner);

        mappings.put(owner + '.' + fieldWrapper.getOriginalName() + '.' + fieldWrapper.getOriginalDescription(), newName);

        if (!Modifier.isStatic(fieldWrapper.getFieldNode().access)) { // Static fields can't be overridden
            tree.getParentClasses().forEach(parentClass -> genFieldMappings(fieldWrapper, parentClass, newName));
            tree.getSubClasses().forEach(subClass -> genFieldMappings(fieldWrapper, subClass, newName));
        }
    }

    private boolean cannotRenameField(ClassTree tree, FieldWrapper wrapper, Set<String> visited) {
        String check = tree.getClassWrapper().getOriginalName() + '.' + wrapper.getOriginalName() + '.' + wrapper.getOriginalDescription();

        // Don't check these
        if (visited.contains(check))
            return false;

        visited.add(check);

        // If excluded, we don't want to rename.
        // If we already mapped the tree, we don't want to waste time doing it again.
        if (excluded(check) || mappings.containsKey(check))
            return true;

        // Fields which are static don't need to be checked for inheritance
        if (!Modifier.isStatic(wrapper.getFieldNode().access)) {
            // We can't rename members which inherit methods from external libraries
            if (tree.getClassWrapper() != wrapper.getOwner() && tree.getClassWrapper().isLibraryNode()
                    && tree.getClassWrapper().getFields().stream().anyMatch(fw -> fw.getOriginalName().equals(wrapper.getOriginalName())
                    && fw.getOriginalDescription().equals(wrapper.getOriginalDescription())))
                return true;

            return tree.getParentClasses().stream().anyMatch(parent -> cannotRenameField(radon.getTree(parent), wrapper, visited))
                    || (tree.getSubClasses().stream().anyMatch(sub -> cannotRenameField(radon.getTree(sub), wrapper, visited)));
        }

        return false;
    }

    private void dumpMappings() {
        long current = System.currentTimeMillis();
        Main.info("Dumping mappings.");
        File file = new File("mappings.txt");
        if (file.exists())
            FileUtils.renameExistingFile(file);

        try {
            file.createNewFile(); // TODO: handle this properly
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            mappings.forEach((oldName, newName) -> {
                try {
                    bw.append(oldName).append(" -> ").append(newName).append('\n');
                } catch (IOException ioe) {
                    Main.severe(String.format("Ran into an error trying to append \"%s -> %s\"", oldName, newName));
                    ioe.printStackTrace();
                }
            });

            bw.close();
            Main.info(String.format("Finished dumping mappings at %s. [%dms]", file.getAbsolutePath(),
                    tookThisLong(current)));
        } catch (Throwable t) {
            Main.severe("Ran into an error trying to create the mappings file.");
            t.printStackTrace();
        }
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.RENAMER;
    }

    @Override
    public String getName() {
        return "Renamer";
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(RenamerSetting.ADAPT_THESE_RESOURCES.getName(), getAdaptTheseResources());
        config.put(RenamerSetting.DUMP_MAPPINGS.getName(), isDumpMappings());
        config.put(RenamerSetting.REPACKAGE_NAME.getName(), getRepackageName());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setAdaptTheseResources(getValueOrDefault(RenamerSetting.ADAPT_THESE_RESOURCES.getName(), config, new ArrayList<String>()).toArray(new String[0]));
        setDumpMappings(getValueOrDefault(RenamerSetting.DUMP_MAPPINGS.getName(), config, false));
        setRepackageName(getValueOrDefault(RenamerSetting.REPACKAGE_NAME.getName(), config, null));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            RenamerSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.RENAMER.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.RENAMER.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    private String[] getAdaptTheseResources() {
        return adaptTheseResources;
    }

    private void setAdaptTheseResources(String[] adaptTheseResources) {
        this.adaptTheseResources = adaptTheseResources;
    }

    public boolean isDumpMappings() {
        return dumpMappings;
    }

    public void setDumpMappings(boolean dumpMappings) {
        this.dumpMappings = dumpMappings;
    }

    private String getRepackageName() {
        return repackageName;
    }

    private void setRepackageName(String repackageName) {
        this.repackageName = repackageName;
    }
}

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.asm.ClassTree;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.asm.MemberRemapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.AccessUtils;
import me.itzsomebody.radon.utils.FileUtils;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;


/**
 * Transformer which renames classes and their members.
 * TODO: Clean this bloody mess up already.
 *
 * @author ItzSomebody
 */
public class Renamer extends Transformer {
    private static final Map<String, RenamerSetting> KEY_MAP = new HashMap<>();
    private String[] adaptTheseResources;
    private String repackageName;
    private Map<String, String> mappings;

    static {
        Stream.of(RenamerSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    private static boolean methodCanBeRenamed(MethodWrapper wrapper) {
        return !AccessUtils.isNative(wrapper.methodNode.access) && !"main".equals(wrapper.originalName)
                && !"premain".equals(wrapper.originalName) && !wrapper.originalName.startsWith("<");
    }

    @Override
    public void transform() {
        mappings = new HashMap<>();

        Logger.stdOut("Generating mappings.");
        long current = System.currentTimeMillis();

        getClassWrappers().forEach(classWrapper -> {
            classWrapper.methods.stream().filter(Renamer::methodCanBeRenamed).forEach(methodWrapper -> {
                String newName = (cannotRenameMethod(radon.getTree(classWrapper.originalName), methodWrapper))
                        ? methodWrapper.originalName : randomString();

                genMethodMappings(methodWrapper, methodWrapper.owner.originalName, newName);
            });

            classWrapper.fields.forEach(fieldWrapper -> {
                String newName = (cannotRenameField(radon.getTree(classWrapper.originalName), fieldWrapper))
                        ? fieldWrapper.originalName : randomString();

                genFieldMappings(fieldWrapper, fieldWrapper.owner.originalName, newName);
            });

            if (!excluded(classWrapper))
                mappings.put(classWrapper.originalName, (getRepackageName() != null) ? getRepackageName() + '/'
                        + randomString() : randomString());
        });

        Logger.stdOut(String.format("Finished generated mappings. [%dms]", tookThisLong(current)));
        Logger.stdOut("Applying mappings.");
        current = System.currentTimeMillis();

        // Apply mappings
        Remapper simpleRemapper = new MemberRemapper(mappings);
        new ArrayList<>(getClassWrappers()).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            ClassNode copy = new ClassNode();
            classNode.accept(new ClassRemapper(copy, simpleRemapper));

            // In order to preserve the original names to prevent exclusions from breaking,
            // we reassign the MethodNode/FieldNode/ClassNode each wrapper wraps instead.
            for (int i = 0; i < copy.methods.size(); i++)
                classWrapper.methods.get(i).methodNode = copy.methods.get(i);

            if (copy.fields != null)
                for (int i = 0; i < copy.fields.size(); i++)
                    classWrapper.fields.get(i).fieldNode = copy.fields.get(i);

            classWrapper.classNode = copy;

            getClasses().remove(classWrapper.originalName);
            getClasses().put(classWrapper.classNode.name, classWrapper);
            getClassPath().put(classWrapper.classNode.name, classWrapper);
        });

        Logger.stdOut(String.format("Mapped %d members. [%dms]", mappings.size(), tookThisLong(current)));
        current = System.currentTimeMillis();

        // Now we gotta fix those resources because we probably screwed up random files.
        Logger.stdOut("Attempting to map class names in resources");
        AtomicInteger fixed = new AtomicInteger();
        getResources().forEach((name, byteArray) -> {
            if (getAdaptTheseResources() != null)
                Stream.of(getAdaptTheseResources()).forEach(s -> {
                    Pattern pattern = Pattern.compile(s);

                    if (pattern.matcher(name).matches()) {
                        String stringVer = new String(byteArray);

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

        Logger.stdOut(String.format("Mapped %d names in resources. [%dms]", fixed.get(), tookThisLong(current)));

        dumpMappings();
    }

    private void genMethodMappings(MethodWrapper methodWrapper, String owner, String newName) {
        // This (supposedly) will always stop the recursion because the tree was already renamed
        if (mappings.containsKey(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription))
            return;

        ClassTree tree = radon.getTree(owner);

        mappings.put(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription, newName);

        if (!Modifier.isStatic(methodWrapper.methodNode.access)) { // Static methods can't be overridden
            tree.parentClasses.forEach(parentClass -> genMethodMappings(methodWrapper, parentClass, newName));
            tree.subClasses.forEach(subClass -> genMethodMappings(methodWrapper, subClass, newName));
        }
    }

    private boolean cannotRenameMethod(ClassTree tree, MethodWrapper wrapper) {
        return (excluded(tree.classWrapper.originalName + '.' + wrapper.originalName + wrapper.originalDescription))
                || (tree.classWrapper.libraryNode)
                || (tree.parentClasses.stream().anyMatch(s -> cannotRenameMethod(radon.getTree(s), wrapper)))
                || (tree.subClasses.stream().anyMatch(s -> cannotRenameMethod(radon.getTree(s), wrapper)));
    }

    private void genFieldMappings(FieldWrapper fieldWrapper, String owner, String newName) {
        // This (supposedly) will always stop the recursion because the tree was already renamed
        if (mappings.containsKey(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription))
            return;

        ClassTree tree = radon.getTree(owner);

        mappings.put(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription, newName);

        if (!Modifier.isStatic(fieldWrapper.fieldNode.access)) { // Static fields can't be overridden
            tree.parentClasses.forEach(parentClass -> genFieldMappings(fieldWrapper, parentClass, newName));
            tree.subClasses.forEach(subClass -> genFieldMappings(fieldWrapper, subClass, newName));
        }
    }

    private boolean cannotRenameField(ClassTree tree, FieldWrapper wrapper) {
        return (excluded(tree.classWrapper.originalName + '.' + wrapper.originalName + '.' + wrapper.originalDescription))
                || (tree.classWrapper.libraryNode)
                || (tree.parentClasses.stream().anyMatch(s -> cannotRenameField(radon.getTree(s), wrapper)))
                || (tree.subClasses.stream().anyMatch(s -> cannotRenameField(radon.getTree(s), wrapper)));
    }

    private void dumpMappings() {
        long current = System.currentTimeMillis();
        Logger.stdOut("Dumping mappings.");
        File file = new File("mappings.txt");
        if (file.exists())
            FileUtils.renameExistingFile(file);

        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            mappings.forEach((oldName, newName) -> {
                try {
                    bw.append(oldName).append(" -> ").append(newName).append('\n');
                } catch (IOException ioe) {
                    Logger.stdErr(String.format("Ran into an error trying to append \"%s -> %s\"", oldName,
                            newName));
                    ioe.printStackTrace();
                }
            });

            bw.close();
            Logger.stdOut(String.format("Finished dumping mappings at %s. [%dms]", file.getAbsolutePath(),
                    tookThisLong(current)));
        } catch (Throwable t) {
            Logger.stdErr("Ran into an error trying to create the mappings file.");
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
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(RenamerSetting.ADAPT_THESE_RESOURCES.getName(), getAdaptTheseResources());
        config.put(RenamerSetting.REPACKAGE_NAME.getName(), getRepackageName());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setAdaptTheseResources(getValueOrDefault(RenamerSetting.ADAPT_THESE_RESOURCES.getName(), config, new String[0]));
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

    private String getRepackageName() {
        return repackageName;
    }

    private void setRepackageName(String repackageName) {
        this.repackageName = repackageName;
    }
}

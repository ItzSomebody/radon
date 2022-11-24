/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import xyz.itzsomebody.radon.Radon;
import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.config.ConfigurationParser;
import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.dictionaries.DictionaryFactory;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.IOUtils;
import xyz.itzsomebody.radon.utils.RandomUtils;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.asm.FieldWrapper;
import xyz.itzsomebody.radon.utils.asm.MethodWrapper;
import xyz.itzsomebody.radon.utils.asm.RadonRemapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;

public class Renamer extends Transformer {
    @JsonProperty("resources_to_adapt")
    private List<String> resourceToAdapt = Collections.emptyList();

    @JsonProperty("dump_mappings")
    private boolean dumpMappings;

    @JsonProperty("repackaging_prefix")
    private String repackagingPrefix;

    @JsonProperty("class_name_dictionary")
    private Dictionary classNameDictionary = DictionaryFactory.defaultDictionary();

    @JsonProperty("method_name_dictionary")
    private Dictionary methodNameDictionary = DictionaryFactory.defaultDictionary();

    @JsonProperty("field_name_dictionary")
    private Dictionary fieldNameDictionary = DictionaryFactory.defaultDictionary();
    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void transform() {
        RadonLogger.info("Building inheritance hierarchy graph");
        long current = System.currentTimeMillis();
        Radon.getInstance().buildHierarchyGraph();
        RadonLogger.info(String.format("Finished building inheritance graph [%dms]", (System.currentTimeMillis() - current)));

        RadonLogger.info("Generating mappings");
        current = System.currentTimeMillis();
        generateMappings();
        RadonLogger.info(String.format("Finished generating mappings [%dms]", (System.currentTimeMillis() - current)));

        RadonLogger.info("Applying mappings");
        current = System.currentTimeMillis();
        applyMappings();
        RadonLogger.info(String.format("Finished applying mappings [%dms]", (System.currentTimeMillis() - current)));

        RadonLogger.info("Adapting resources");
        current = System.currentTimeMillis();
        adaptResources();
        RadonLogger.info(String.format("Finished adapting resources [%dms]", (System.currentTimeMillis() - current)));

        if (dumpMappings) {
            RadonLogger.info("Dumping mappings");
            dumpMappings();
        }
    }

    private void generateMappings() {
        classes().forEach(classWrapper -> {
            classWrapper.methodStream().filter(methodWrapped -> !cannotRenameMethod(classWrapper, methodWrapped, new HashSet<>())).forEach(methodWrapper -> {
                String newName;
                do {
                    newName = methodNameDictionary.next();
                } while (mappings.containsKey(classWrapper.getOriginalName() + '.' + methodWrapper.getOriginalName() + methodWrapper.getOriginalDescriptor())
                        && mappings.get(classWrapper.getOriginalName() + '.' + methodWrapper.getOriginalName() + methodWrapper.getOriginalDescriptor()).equals(newName));


                generateMethodMappings(classWrapper, methodWrapper, newName);
                for (var mapping : mappings.keySet()) {
                    if (mapping.startsWith("java/util/List.size") && mappings.get(mapping).contains("Cx")) {
                        System.out.println("Working on " + classWrapper.getName() + '.' + methodWrapper.getOriginalName() + methodWrapper.getOriginalDescriptor() + ": " + mapping + " -> " + mappings.get(mapping));
                        break;
                    }
                }
            });
            classWrapper.fieldStream().filter(fieldWrapper -> !cannotRenameField(classWrapper, fieldWrapper)).forEach(fieldWrapper -> {
                String newName;
                do {
                    newName = fieldNameDictionary.next();
                } while (mappings.containsKey(classWrapper.getOriginalName() + '.' + fieldWrapper.getOriginalName() + ' ' + fieldWrapper.getOriginalType())
                        && mappings.get(classWrapper.getOriginalName() + '.' + fieldWrapper.getOriginalName() + ' ' + fieldWrapper.getOriginalType()).equals(newName));


                generateFieldMappings(classWrapper, fieldWrapper, newName);
            });

            if (notExcluded(classWrapper)) {
                if (repackagingPrefix == null) {
                    repackagingPrefix = classNameDictionary.copy().randomStr(RandomUtils.randomInt(0xF));
                }

                var newName = repackagingPrefix;
                if (!newName.isEmpty()) {
                    newName += '/';
                }
                String temp;
                do {
                    temp = newName + classNameDictionary.next();
                } while (classPathMap().containsKey(temp)); // Important to check classpath instead of input classes
                newName = temp;

                mappings.put(classWrapper.getOriginalName(), newName);
            }
        });
    }

    private boolean cannotRenameMethod(ClassWrapper classWrapper, MethodWrapper wrapper, Set<ClassWrapper> visited) {
        // Already visited so don't check
        if (!visited.add(classWrapper)) {
            return false;
        }

        // If excluded, we don't want to rename.
        // If we already mapped the tree, we don't want to waste time doing it again.
        if (!notExcluded(classWrapper.getOriginalName() + '.' + wrapper.getOriginalName() + wrapper.getOriginalDescriptor()) || mappings.containsKey(classWrapper.getOriginalName() + '.' + wrapper.getOriginalName() + wrapper.getOriginalDescriptor())) {
            return true;
        }

        // Native and main/premain methods should not be renamed
        // Init and clinit methods should also not be renamed (otherwise the jvm will get mad)
        if (wrapper.isNative() || wrapper.getOriginalName().equals("main") || wrapper.getOriginalName().equals("premain") || wrapper.getOriginalName().startsWith("<")) {
            return true;
        }

        // Static methods are never inherited
        if (wrapper.isStatic()) {
            // Renaming these particular enum methods will cause problems
            return classWrapper.isEnum()
                    && (wrapper.getOriginalName().equals("valueOf") || wrapper.getOriginalName().equals("values"));
        } else {
            if (classWrapper.getOriginalName().startsWith("java/util")) {
                System.out.println("Checking " + classWrapper.getOriginalName() + '.' + wrapper.getOriginalName() + wrapper.getOriginalDescriptor());
            }
            // Methods which override or inherit from external libs cannot be renamed
            if (classWrapper != wrapper.getOwner() && classWrapper.isLibraryNode()
                    && classWrapper.methodStream().anyMatch(other -> other.getOriginalName().equals(wrapper.getOriginalName())
                    && other.getOriginalDescriptor().equals(wrapper.getOriginalDescriptor()))) {
                return true;
            }

            // Children are checked for exclusions
            // Parents are checked for exclusions and if they are library nodes
            return classWrapper.getParents().stream().anyMatch(parent -> cannotRenameMethod(parent, wrapper, visited))
                    || classWrapper.getChildren().stream().anyMatch(child -> cannotRenameMethod(child, wrapper, visited));
        }
    }

    private void generateMethodMappings(ClassWrapper owner, MethodWrapper wrapper, String newName) {
        String key = owner.getOriginalName() + '.' + wrapper.getOriginalName() + wrapper.getOriginalDescriptor();

        // This (supposedly) will prevent an infinite recursion because the tree was already renamed
        if (mappings.containsKey(key)) {
            return;
        }
        mappings.put(key, newName);

        if (!wrapper.isStatic()) { //  Static methods cannot be overriden
            owner.getParents().forEach(parent -> generateMethodMappings(parent, wrapper, newName));
            owner.getChildren().forEach(child -> generateMethodMappings(child, wrapper, newName));
        }
    }

    private boolean cannotRenameField(ClassWrapper classWrapper, FieldWrapper wrapper) {
        if (!notExcluded(wrapper) || mappings.containsKey(classWrapper.getOriginalName() + '.' + wrapper.getOriginalName() + ' ' + wrapper.getOriginalType())) {
            return true;
        }

        return classWrapper.isEnum(); // Todo: enums are a pain to handle
    }

    private void generateFieldMappings(ClassWrapper owner, FieldWrapper wrapper, String newName) {
        String key = owner.getOriginalName() + '.' + wrapper.getOriginalName() + ' ' + wrapper.getOriginalType();

        // This (supposedly) will prevent an infinite recursion because the tree was already renamed
        if (mappings.containsKey(key)) {
            return;
        }
        mappings.put(key, newName);

        if (!wrapper.isStatic()) { //  Static fields cannot be inherited
            owner.getParents().forEach(parent -> generateFieldMappings(parent, wrapper, newName));
            owner.getChildren().forEach(child -> generateFieldMappings(child, wrapper, newName));
        }
    }

    private void applyMappings() {
        var remapper = new RadonRemapper(mappings);
        new ArrayList<>(classes()).forEach(classWrapper -> {
            var classNode = classWrapper.getClassNode();
            var copy = new ClassNode();
            classNode.accept(new ClassRemapper(copy, remapper));

            // In order to preserve the original names to prevent exclusions from breaking,
            // we update the MethodNode/FieldNode/ClassNode each wrapper wraps instead.
            IntStream.range(0, copy.methods.size()).forEach(i -> {
                classWrapper.getMethods().get(i).setMethodNode(copy.methods.get(i));
            });
            IntStream.range(0, copy.fields.size()).forEach(i -> {
                classWrapper.getFields().get(i).setFieldNode(copy.fields.get(i));
            });
            classWrapper.setClassNode(copy);

            // Fix input/classpath classnames
            classMap().remove(classWrapper.getOriginalName());
            classPathMap().remove(classWrapper.getOriginalName());
            classMap().put(copy.name, classWrapper);
            classPathMap().put(copy.name, classWrapper);
        });
    }

    private void adaptResources() {
        resourceToAdapt.forEach(resourceName -> {
            var resourceBytes = resourceMap().get(resourceName);
            if (resourceBytes == null) {
                RadonLogger.warn("Attempted to adapt nonexistent resource: " + resourceName);
            }

            var stringVer = new String(resourceBytes, StandardCharsets.UTF_8);
            for (var original : mappings.keySet()) {
                if (stringVer.contains(original.replace("/", "."))) {
                    if (resourceName.equals("META-INF/MANIFEST.MF")
                            || resourceName.equals("plugin.yml")
                            || resourceName.equals("bungee.yml")) {
                        stringVer = stringVer.replaceAll("(?<=[: ])" + original.replace("/", "."), mappings.get(original)).replace("/", ".");
                    } else {
                        stringVer = stringVer.replace(original.replace("/", "."), mappings.get(original)).replace("/", ".");
                    }
                }
            }
            resourceMap().put(resourceName, stringVer.getBytes(StandardCharsets.UTF_8));
        });
    }

    private void dumpMappings() {
        var file = new File("mappings.txt");
        if (file.exists()) {
            IOUtils.renameExistingFile(file);
        }

        try {
            file.createNewFile();
            var bw = new BufferedWriter(new FileWriter(file));
            mappings.forEach((oldName, newName) -> {
                try {
                    bw.append(oldName).append(" -> ").append(newName).append("\n");
                } catch (IOException ioe) {
                    RadonLogger.warn(String.format("Caught IOException while attempting to write line \"%s -> %s\"", oldName, newName));
                    if (RadonConstants.VERBOSE) {
                        ioe.printStackTrace(System.out);
                    }
                }
            });
        } catch (Throwable t) {
            RadonLogger.warn("Captured throwable upon attempting to generate mappings file: " + t.getMessage());
            if (RadonConstants.VERBOSE) {
                t.printStackTrace(System.out);
            }
        }
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.RENAMER;
    }

    @Override
    public String getConfigName() {
        return Transformers.RENAMER.getConfigName();
    }
}

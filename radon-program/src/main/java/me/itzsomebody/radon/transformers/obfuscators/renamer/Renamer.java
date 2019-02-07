/*
 * Copyright (C) 2018 ItzSomebody
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import me.itzsomebody.radon.asm.ClassTree;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.asm.MemberRemapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.AccessUtils;
import me.itzsomebody.radon.utils.IOUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * Transformer which renames classes and their members.
 *
 * @author ItzSomebody
 */
public class Renamer extends Transformer {
    private RenamerSetup setup;
    private HashMap<String, String> mappings = new HashMap<>();

    public Renamer(RenamerSetup setup) {
        this.setup = setup;
    }

    @Override
    public void transform() {
        LoggerUtils.stdOut("Generating mappings.");
        long current = System.currentTimeMillis();
        AtomicInteger classCounter = new AtomicInteger();
        this.getClassWrappers().forEach(classWrapper -> {
            classWrapper.methods.stream().filter(methodWrapper -> !AccessUtils.isNative(methodWrapper.methodNode.access)
                    && !"main".equals(methodWrapper.methodNode.name) && !"premain".equals(methodWrapper.methodNode.name)
                    && !methodWrapper.methodNode.name.startsWith("<")).forEach(methodWrapper -> {
                if (canRenameMethodTree(new HashSet<>(), methodWrapper, classWrapper.originalName)) {
                    this.renameMethodTree(new HashSet<>(), methodWrapper, classWrapper.originalName, randomString(4));
                }
            });

            classWrapper.fields.forEach(fieldWrapper -> {
                if (canRenameFieldTree(new HashSet<>(), fieldWrapper, classWrapper.originalName)) {
                    this.renameFieldTree(new HashSet<>(), fieldWrapper, classWrapper.originalName, randomString(4));
                }
            });

            if (!this.excluded(classWrapper)) {
                this.mappings.put(classWrapper.originalName, (setup.getRepackageName() != null)
                        ? setup.getRepackageName() + '/' + randomString(4) : randomString(4));
                classCounter.incrementAndGet();
            }
        });
        LoggerUtils.stdOut(String.format("Finished generated mappings. [%dms]", tookThisLong(current)));
        LoggerUtils.stdOut("Applying mappings.");
        current = System.currentTimeMillis();

        // Apply mapping
        Remapper simpleRemapper = new MemberRemapper(this.mappings);
        for (ClassWrapper classWrapper : new ArrayList<>(this.getClassWrappers())) {
            ClassNode classNode = classWrapper.classNode;

            ClassNode copy = new ClassNode();
            classNode.accept(new ClassRemapper(copy, simpleRemapper));
            for (int i = 0; i < copy.methods.size(); i++) {
                classWrapper.methods.get(i).methodNode = copy.methods.get(i);

                /*for (AbstractInsnNode insn : methodNode.instructions.toArray()) { // TODO: Fix lambdas + interface
                    if (insn instanceof InvokeDynamicInsnNode) {
                        InvokeDynamicInsnNode indy = (InvokeDynamicInsnNode) insn;
                        if (indy.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")) {
                            Handle handle = (Handle) indy.bsmArgs[1];
                            String newName = mappings.get(handle.getOwner() + '.' + handle.getName() + handle.getDesc());

                            if (newName != null) {
                                indy.name = newName;
                                indy.bsm = new Handle(handle.getTag(), handle.getOwner(), newName, handle.getDesc(), false);
                            }
                        }
                    }
                }*/
            }

            if (copy.fields != null) {
                for (int i = 0; i < copy.fields.size(); i++) {
                    classWrapper.fields.get(i).fieldNode = copy.fields.get(i);
                }
            }

            classWrapper.classNode = copy;
            this.getClasses().remove(classWrapper.originalName);
            this.getClasses().put(classWrapper.classNode.name, classWrapper);
            this.getClassPath().put(classWrapper.classNode.name, classWrapper);
        }

        LoggerUtils.stdOut(String.format("Mapped %d members. [%dms]", mappings.size(), tookThisLong(current)));
        current = System.currentTimeMillis();

        // Fix screw ups in resources.
        LoggerUtils.stdOut("Attempting to map class names in resources");
        AtomicInteger fixed = new AtomicInteger();
        getResources().forEach((name, byteArray) -> {
            if (setup.getAdaptTheseResources() != null) {
                for (String s : setup.getAdaptTheseResources()) {
                    Pattern pattern = Pattern.compile(s);

                    if (pattern.matcher(name).matches()) {
                        String stringVer = new String(byteArray);
                        for (String mapping : mappings.keySet()) {
                            String original = mapping.replace("/", ".");
                            if (stringVer.contains(original)) {
                                // Regex that ensures that class names that match words in the manifest don't break the
                                // manifest.
                                // Example: name == Main
                                if ("META-INF/MANIFEST.MF".equals(name) || "plugin.yml".equals(name)
                                        || "bungee.yml".equals(name)) {
                                    stringVer = stringVer.replaceAll("(?<=[: ])"
                                            + original, mappings.get(mapping).replace("/", "."));
                                } else {
                                    stringVer = stringVer.replace(original, mappings.get(mapping).replace("/", "."));
                                }
                            }
                        }

                        getResources().put(name, stringVer.getBytes(StandardCharsets.UTF_8));
                        fixed.incrementAndGet();
                    }
                }
            }
        });
        LoggerUtils.stdOut(String.format("Mapped %d names in resources. [%dms]", fixed.get(), tookThisLong(current)));
        dumpMappings();
    }

    private boolean canRenameMethodTree(HashSet<ClassTree> visited, MethodWrapper methodWrapper, String owner) {
        ClassTree tree = this.radon.getTree(owner);
        if (!visited.contains(tree)) {
            visited.add(tree);
            if (excluded(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription)) {
                return false;
            }
            if (mappings.containsKey(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription)) {
                return true;
            }
            if (!methodWrapper.owner.originalName.equals(owner) && tree.classWrapper.libraryNode) {
                for (MethodNode mn : tree.classWrapper.classNode.methods) {
                    if (mn.name.equals(methodWrapper.originalName)
                            & mn.desc.equals(methodWrapper.originalDescription)) {
                        return false;
                    }
                }
            }
            for (String parent : tree.parentClasses) {
                if (parent != null && !canRenameMethodTree(visited, methodWrapper, parent)) {
                    return false;
                }
            }
            for (String sub : tree.subClasses) {
                if (sub != null && !canRenameMethodTree(visited, methodWrapper, sub)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void renameMethodTree(HashSet<ClassTree> visited, MethodWrapper MethodWrapper, String className,
                                  String newName) {
        ClassTree tree = this.radon.getTree(className);

        if (!tree.classWrapper.libraryNode && !visited.contains(tree)) {
            mappings.put(className + '.' + MethodWrapper.originalName + MethodWrapper.originalDescription, newName);
            visited.add(tree);
            for (String parentClass : tree.parentClasses) {
                this.renameMethodTree(visited, MethodWrapper, parentClass, newName);
            }
            for (String subClass : tree.subClasses) {
                this.renameMethodTree(visited, MethodWrapper, subClass, newName);
            }
        }
    }

    private boolean canRenameFieldTree(HashSet<ClassTree> visited, FieldWrapper fieldWrapper, String owner) {
        ClassTree tree = this.radon.getTree(owner);
        if (!visited.contains(tree)) {
            visited.add(tree);
            if (excluded(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription)) {
                return false;
            }
            if (mappings.containsKey(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription))
                return true;
            if (!fieldWrapper.owner.originalName.equals(owner) && tree.classWrapper.libraryNode) {
                for (FieldNode fn : tree.classWrapper.classNode.fields) {
                    if (fieldWrapper.originalName.equals(fn.name) && fieldWrapper.originalDescription.equals(fn.desc)) {
                        return false;
                    }
                }
            }
            for (String parent : tree.parentClasses) {
                if (parent != null && !canRenameFieldTree(visited, fieldWrapper, parent)) {
                    return false;
                }
            }
            for (String sub : tree.subClasses) {
                if (sub != null && !canRenameFieldTree(visited, fieldWrapper, sub)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void renameFieldTree(HashSet<ClassTree> visited, FieldWrapper fieldWrapper, String owner, String newName) {
        ClassTree tree = this.radon.getTree(owner);

        if (!tree.classWrapper.libraryNode && !visited.contains(tree)) {
            mappings.put(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription, newName);
            visited.add(tree);
            for (String parentClass : tree.parentClasses) {
                this.renameFieldTree(visited, fieldWrapper, parentClass, newName);
            }
            for (String subClass : tree.subClasses) {
                this.renameFieldTree(visited, fieldWrapper, subClass, newName);
            }
        }
    }

    private void dumpMappings() {
        long current = System.currentTimeMillis();
        LoggerUtils.stdOut("Dumping mappings.");
        File file = new File("mappings.txt");
        if (file.exists()) {
            IOUtils.renameExistingFile(file);
        }

        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            mappings.forEach((oldName, newName) -> {
                try {
                    bw.append(oldName).append(" -> ").append(newName).append('\n');
                } catch (IOException ioe) {
                    LoggerUtils.stdErr(String.format("Ran into an error trying to append \"%s -> %s\"", oldName,
                            newName));
                    ioe.printStackTrace();
                }
            });

            bw.close();
            LoggerUtils.stdOut(String.format("Finished dumping mappings at %s. [%dms]", file.getAbsolutePath(),
                    tookThisLong(current)));
        } catch (Throwable t) {
            LoggerUtils.stdErr("Ran into an error trying to create the mappings file.");
            t.printStackTrace();
        }
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.RENAMER;
    }

    @Override
    public String getName() {
        return "Renamer";
    }

    public RenamerSetup getSetup() {
        return setup;
    }
}

package me.itzsomebody.radon.transformers;

import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that renames classes and their members.
 * TODO: Very inefficient method of generating method names, work on it.
 *
 * @author ItzSomebody
 */
public class Renamer extends AbstractTransformer {
    /**
     * Used for class, method and field renamming. Format is oldName -> newName.
     */
    private Map<String, String> mappings = new HashMap<>();

    /**
     * Indication to look for Bukkit/Bungee main methods.
     */
    private boolean spigotMode;

    /**
     * Constructor used to create a {@link Renamer} object.
     */
    public Renamer(boolean spigotMode) {
        this.spigotMode = spigotMode;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Generating mappings."));
        long current = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + '.' + methodNode.desc))
                    .filter(methodNode -> !BytecodeUtils.isNativeMethod(methodNode.access)
                                    && !methodNode.name.equals("main") && !methodNode.name.equals("premain")
                                    && !methodNode.name.startsWith("<")
                            /*&& !mappings.containsKey(classNode.name + '.' + methodNode.name + methodNode.desc)*/).forEach(methodNode -> {

                boolean doNotRename = false;
                if (classNode.superName != null) {
                    ClassNode superClass = getClassNode(classNode.superName);
                    for (MethodNode superMethod : superClass.methods) {
                        if (methodNode.name.equals(superMethod.name)
                                && methodNode.desc.equals(superMethod.desc)) {
                            if (superClass.libraryNode) {
                                doNotRename = true;
                                continue;
                            }
                            String newName = StringUtils.crazyString();
                            mappings.put(classNode.name + '.' + methodNode.name + methodNode.desc, newName);

                            for (ClassNode cn : classNodes()) {
                                if (cn.name.equals(classNode.superName)
                                        || classNode.interfaces.contains(cn.name)
                                        || cn.superName.equals(classNode.name)
                                        || cn.interfaces.contains(classNode.name)) {
                                    for (MethodNode mn : cn.methods) {
                                        if (mn.name.equals(methodNode.name)
                                                && mn.desc.equals(methodNode.desc)) {
                                            mappings.put(cn.name + '.' + mn.name + mn.desc, newName);
                                            doNotRename = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (classNode.interfaces != null) {
                    for (String className : classNode.interfaces) {
                        ClassNode interfaceClass = getClassNode(className);
                        for (MethodNode interfaceMethod : interfaceClass.methods) {
                            if (methodNode.name.equals(interfaceMethod.name)
                                    && methodNode.desc.equals(interfaceMethod.desc)) {
                                if (interfaceClass.libraryNode) {
                                    doNotRename = true;
                                    continue;
                                }
                                String newName = StringUtils.crazyString();
                                mappings.put(classNode.name + '.' + methodNode.name + methodNode.desc, newName);

                                for (ClassNode cn : classNodes()) {
                                    if (cn.name.equals(classNode.superName)
                                            || classNode.interfaces.contains(cn.name)
                                            || cn.superName.equals(classNode.name)
                                            || cn.interfaces.contains(classNode.name)) {
                                        for (MethodNode mn : cn.methods) {
                                            if (mn.name.equals(methodNode.name)
                                                    && mn.desc.equals(methodNode.desc)) {
                                                mappings.put(cn.name + '.' + mn.name + mn.desc, newName);
                                                doNotRename = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!doNotRename) {
                    mappings.put(classNode.name + '.' + methodNode.name + methodNode.desc, StringUtils.crazyString());
                }
            });

            if (classNode.fields != null) classNode.fields.stream()
                    .filter(fieldNode -> !fieldExempted(classNode.name + '.' + fieldNode.name)).forEach(fieldNode -> {
                        mappings.put(classNode.name + '.' + fieldNode.name, StringUtils.crazyString());
                        counter.incrementAndGet();
                    });

            if (!BytecodeUtils.isMain(classNode, spigotMode)) {
                switch (NumberUtils.getRandomInt(3)) {
                    case 0:
                        mappings.put(classNode.name, StringUtils.crazyString());
                        break;
                    case 1:
                        mappings.put(classNode.name, StringUtils.crazyString() + '/' + StringUtils.crazyString());
                        break;
                    case 2:
                        mappings.put(classNode.name, StringUtils.crazyString() + '/' + StringUtils.crazyString() + '/' + StringUtils.crazyString());
                        break;
                }
            }
            counter.incrementAndGet();
        });
        logStrings.add(LoggerUtils.stdOut("Finished generated mappings. [" + String.valueOf(System.currentTimeMillis() - current) + "ms]"));
        logStrings.add(LoggerUtils.stdOut("Applying mappings."));
        current = System.currentTimeMillis();

        // Apply mapping
        Remapper simpleRemapper = new SimpleRemapper(mappings);
        for (ClassNode classNode : new ArrayList<>(classNodes())) {
            ClassNode copy = new ClassNode();
            classNode.accept(new ClassRemapper(copy, simpleRemapper));
            copy.access = BytecodeUtils.accessFixer(copy.access);
            for (MethodNode methodNode : copy.methods) {
                methodNode.access = BytecodeUtils.accessFixer(methodNode.access);
            }

            if (copy.fields != null) {
                for (FieldNode fieldNode : copy.fields) {
                    fieldNode.access = BytecodeUtils.accessFixer(fieldNode.access);
                }
            }

            getClassMap().remove(classNode.name);
            getClassPathMap().put(copy.name, copy);
            getClassMap().put(copy.name, copy);
        }

        logStrings.add(LoggerUtils.stdOut("Renamed " + counter + " members."));
        logStrings.add(LoggerUtils.stdOut("Finished applying mappings. [" + String.valueOf(System.currentTimeMillis() - current) + "ms]"));
    }
}

package me.itzsomebody.radon.transformers.renamer;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that renames classes and their members.
 * TODO: FIX THIS
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
                            && !mappings.containsKey(classNode.name + '.' + methodNode.name + methodNode.desc)).forEach(methodNode -> {
                boolean doNotRename = false;
                if (classNode.superName != null) {
                    ClassNode superClass = getClassNode(classNode.superName);
                    outer: {
                        while (superClass != null && superClass.superName != null) {
                            for (MethodNode superMethod : superClass.methods) {
                                if (methodNode.name.equals(superMethod.name)
                                        && methodNode.desc.equals(superMethod.desc)) {
                                    if (superClass.libraryNode) {
                                        doNotRename = true;
                                        break outer;
                                    }
                                }
                            }

                            if (superClass.interfaces != null) {
                                for (String className : superClass.interfaces) {
                                    ClassNode interfaceClass = getClassNode(className);
                                    for (MethodNode interfaceMethod : interfaceClass.methods) {
                                        if (methodNode.name.equals(interfaceMethod.name)
                                                && methodNode.desc.equals(interfaceMethod.desc)) {
                                            if (interfaceClass.libraryNode) {
                                                doNotRename = true;
                                                break outer;
                                            }
                                        }
                                    }

                                /*if (interfaceClass.interfaces != null) {
                                    for (String subInterface : interfaceClass.interfaces) {
                                        ClassNode subInterfaceClass = getClassNode(subInterface);
                                        while (interfaceClass.interfaces != null || !doNotRename) {
                                            deletethis.println("Starting while loop 2");
                                            for (MethodNode subInterfaceMethod : subInterfaceClass.methods) {
                                                if (methodNode.name.equals(subInterfaceClass.name)
                                                        && methodNode.desc.equals(subInterfaceMethod.desc)) {
                                                    if (subInterfaceClass.libraryNode) {
                                                        deletethis.println("The interface class was a lib, don't setting doNotRename");
                                                        doNotRename = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }*/
                                }
                            }

                            superClass = getClassNode(superClass.superName);
                        }
                    }
                }

                outer: {
                    if (!doNotRename && classNode.interfaces != null) {
                        for (String className : classNode.interfaces) {
                            ClassNode interfaceClass = getClassNode(className);
                            //while (interfaceClass != null && interfaceClass.interfaces != null && !doNotRename) {
                            if (!interfaceClass.methods.isEmpty()) {
                                for (MethodNode interfaceMethod : interfaceClass.methods) {
                                    if (methodNode.name.equals(interfaceMethod.name)
                                            && methodNode.desc.equals(interfaceMethod.desc)) {
                                        if (interfaceClass.libraryNode) {
                                            doNotRename = true;
                                            break outer;
                                        }
                                    }
                                }
                            }
                            //}
                        }
                    }
                }

                if (!doNotRename) {
                    String newName = StringUtils.crazyString();
                    mappings.put(classNode.name + '.' + methodNode.name + methodNode.desc, newName);
                    ClassNode superClass = getClassNode(classNode.superName);
                    while (superClass != null && superClass.superName != null) {
                        for (MethodNode superMethod : superClass.methods) {
                            if (methodNode.name.equals(superMethod.name)
                                    && methodNode.desc.equals(superMethod.desc)) {
                                mappings.put(superClass.name + '.' + methodNode.name + methodNode.desc, newName);
                            }
                        }

                        for (String className : superClass.interfaces) {
                            ClassNode interfaceClass = getClassNode(className);
                            //while (interfaceClass != null && interfaceClass.interfaces != null) {
                            for (MethodNode interfaceMethod : interfaceClass.methods) {
                                if (methodNode.name.equals(interfaceMethod.name)
                                        && methodNode.desc.equals(interfaceMethod.desc)) {
                                    mappings.put(interfaceClass.name + '.' + methodNode.name + methodNode.desc, newName);
                                }
                                //}
                            }
                        }

                        superClass = getClassNode(superClass.superName);
                    }

                    if (classNode.interfaces != null) {
                        for (String className : classNode.interfaces) {
                            ClassNode interfaceClass = getClassNode(className);
                            //while (interfaceClass != null && interfaceClass.interfaces != null) {
                            for (MethodNode interfaceMethod : interfaceClass.methods) {
                                if (methodNode.name.equals(interfaceMethod.name)
                                        && methodNode.desc.equals(interfaceMethod.desc)) {
                                    mappings.put(interfaceClass.name + '.' + methodNode.name + methodNode.desc, newName);
                                }
                            }
                            //  }
                        }
                    }
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
            // Now we have to fix inheritance issues which ASM doesn't freaking fix
            for (MethodNode mn : classNode.methods) {
                if (classNode.superName == null
                        && classNode.interfaces == null) {
                    break; // No inheritance whatsoever
                }
                for (AbstractInsnNode insn : mn.instructions.toArray()) {
                    if (insn instanceof MethodInsnNode) {
                        if (insn.getOpcode() == INVOKEVIRTUAL
                                || insn.getOpcode() == INVOKEINTERFACE) {
                            MethodInsnNode min = (MethodInsnNode) insn;
                            if (min.owner.equals(classNode.name)
                                    && !BytecodeUtils.containsMethod(min.name, min.desc, classNode)) {
                                ClassNode superClass = getClassNode(classNode.superName);
                                if (!superClass.libraryNode) { // Lib node = don't touch.
                                    if (BytecodeUtils.containsMethod(min.name, min.desc, superClass)) {
                                        String key = superClass.name + '.' + min.name + min.desc;
                                        if (mappings.containsKey(key)) {
                                            mappings.put(classNode.name + '.' + min.name + min.desc, mappings.get(key));
                                        }
                                    }
                                }

                                for (String interfaceName : classNode.interfaces) {
                                    ClassNode interfaceClass = getClassNode(interfaceName);
                                    if (!interfaceClass.libraryNode) { // Lib node = don't touch.
                                        if (BytecodeUtils.containsMethod(min.name, min.desc, interfaceClass)) {
                                            String key = interfaceClass.name + '.' + min.name + min.desc;
                                            if (mappings.containsKey(key)) {
                                                mappings.put(classNode.name + '.' + min.name + min.desc, mappings.get(key));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (insn instanceof FieldInsnNode) {
                        if (insn.getOpcode() == GETFIELD
                                || insn.getOpcode() == PUTFIELD) {
                            FieldInsnNode fin = (FieldInsnNode) insn;
                            if (fin.owner.equals(classNode.name)
                                    && !BytecodeUtils.containsField(fin.name, fin.desc, classNode)) {
                                ClassNode superClass = getClassNode(classNode.superName);
                                if (!superClass.libraryNode) { // Lib node = don't touch.
                                    if (BytecodeUtils.containsField(fin.name, fin.desc, superClass)) {
                                        String key = superClass.name + '.' + fin.name;
                                        if (mappings.containsKey(key)) {
                                            mappings.put(classNode.name + '.' + fin.name, mappings.get(key));
                                        }
                                    }
                                }

                                for (String interfaceName : classNode.interfaces) {
                                    ClassNode interfaceClass = getClassNode(interfaceName);
                                    if (!interfaceClass.libraryNode) { // Lib node = don't touch.
                                        if (BytecodeUtils.containsMethod(fin.name, fin.desc, interfaceClass)) {
                                            String key = interfaceClass.name + '.' + fin.name;
                                            if (mappings.containsKey(key)) {
                                                mappings.put(classNode.name + '.' + fin.name, mappings.get(key));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

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
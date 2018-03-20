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

import java.util.*;
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
     * Used to determine how the classes interact with each other.
     */
    private Map<String, ClassTree> hierarchy = new HashMap<>();

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
        createTrees();
        long current = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger();
        classNodes().forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !BytecodeUtils.isNativeMethod(methodNode.access)
                    && !methodNode.name.equals("main")
                    && !methodNode.name.equals("premain")
                    && !methodNode.name.startsWith("<")
                    && !methodNode.name.contains("lambda")).forEach(methodNode -> {
                if (weCanRenameMethod(methodNode)) {
                    String newName = StringUtils.crazyString();
                    renameMethodTree(new ArrayList<>(), methodNode, classNode.name, newName);
                }
            });

            classNode.fields.forEach(fieldNode -> {
                if (weCanRenameField(fieldNode)) {
                    String newName = StringUtils.crazyString();
                    renameFieldTree(new ArrayList<>(), fieldNode, classNode.name, newName);
                }
            });

            if (!classExempted(classNode.name)
                    && !BytecodeUtils.isMain(classNode, spigotMode)) {
                int packages = NumberUtils.getRandomInt(2) + 1;
                String newName = "";
                for (int i = 0; i < packages; i++) {
                    newName += StringUtils.crazyString() + '/';
                }

                mappings.put(classNode.name, newName.substring(0, newName.length() - 1));
                counter.incrementAndGet();
            }
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

        logStrings.add(LoggerUtils.stdOut("Mapped " + counter + " members."));
        logStrings.add(LoggerUtils.stdOut("Finished applying mappings. [" + String.valueOf(System.currentTimeMillis() - current) + "ms]"));
    }

    /**
     * Creates {@link ClassTree}s needed for renaming.
     */
    private void createTrees() {
        logStrings.add(LoggerUtils.stdOut("Creating class hierarchy."));
        long current = System.currentTimeMillis();
        getClassPathMap().values().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.owner = classNode.name;
            });
            classNode.fields.forEach(fieldNode -> {
                fieldNode.owner = classNode.name;
            });
            ClassTree classTree = new ClassTree(classNode.name, classNode.libraryNode);
            classTree.parentClasses.add(classNode.superName);
            classTree.parentClasses.addAll(classNode.interfaces);
            classNodes().stream().filter(node -> node.superName.equals(classNode.name)
                    || node.interfaces.contains(classNode.name)).forEach(node -> {
                classTree.subClasses.add(node.name);
            });

            classTree.methods.addAll(classNode.methods);
            classTree.fields.addAll(classNode.fields);
            hierarchy.put(classNode.name, classTree);
        });
        logStrings.add(LoggerUtils.stdOut("Finished creating class hierarchy. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * Ultimately determines if we can rename a method without running into errors.
     *
     * @param methodNode {@link MethodNode} we want to check if we can rename without causing the JVM to spit lots of errors.
     * @return true if we can rename a method without running into errors.
     */
    private boolean weCanRenameMethod(MethodNode methodNode) {
        if (methodExempted(methodNode.owner + '.' + methodNode.name + methodNode.desc)) {
            return false;
        }
        for (ClassTree ct : hierarchy.values()) {
            if (ct.subClasses.contains(methodNode.owner)
                    && isLibInheritedMN(methodNode, methodNode.owner)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to determine if the method we input is inherited from a library class.
     *
     * @param methodNode {@link MethodNode} we want to check if we can rename without causing the JVM to spit lots of errors.
     * @param className  the name of the class we want to check
     * @return true if the method we input is inherited from a library class.
     */
    private boolean isLibInheritedMN(MethodNode methodNode, String className) {
        ClassTree ct = hierarchy.get(className);
        if (ct != null) {
            if (!methodNode.owner.equals(className)) {
                for (MethodNode mn : ct.methods) {
                    if (mn.name.equals(methodNode.name) && mn.desc.equals(methodNode.desc)) {
                        if (ct.libraryNode) {
                            return true;
                        }
                    }
                }
            }
            for (String parentClass : ct.parentClasses) {
                if (isLibInheritedMN(methodNode, parentClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Renames the methods in an inheritance tree to prevent inheritance errors.
     *
     * @param visited    a list of {@link ClassTree}s which contain the {@link MethodNode}s we already renamed.
     * @param methodNode the method information.
     * @param className  the class we are currently browsing through.
     * @param newName    the new name of the method.
     */
    private void renameMethodTree(List<ClassTree> visited, MethodNode methodNode, String className, String newName) {
        ClassTree ct = hierarchy.get(className);
        if (!ct.libraryNode && !visited.contains(ct)) {
            mappings.put(className + '.' + methodNode.name + methodNode.desc, newName);
            visited.add(ct);
            for (String parentClass : ct.parentClasses) {
                renameMethodTree(visited, methodNode, parentClass, newName);
            }
            for (String subClass : ct.subClasses) {
                renameMethodTree(visited, methodNode, subClass, newName);
            }
        }
    }

    /**
     * Ultimately determines if we can rename a field without running into errors.
     *
     * @param fieldNode {@link FieldNode} we want to check if we can rename without causing the JVM to spit lots of errors.
     * @return true if we can rename a field without running into errors.
     */
    private boolean weCanRenameField(FieldNode fieldNode) {
        if (methodExempted(fieldNode.owner + '.' + fieldNode.name)) {
            return false;
        }
        for (ClassTree ct : hierarchy.values()) {
            if (ct.subClasses.contains(fieldNode.owner)
                    && isLibInheritedFN(fieldNode, fieldNode.owner)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to determine if the method we input is inherited from a library class.
     *
     * @param fieldNode {@link FieldNode} we want to check if we can rename without causing the JVM to spit lots of errors.
     * @param className  the name of the class we want to check
     * @return true if the method we input is inherited from a library class.
     */
    private boolean isLibInheritedFN(FieldNode fieldNode, String className) {
        ClassTree ct = hierarchy.get(className);
        if (ct != null) {
            if (!fieldNode.owner.equals(className)) {
                for (MethodNode mn : ct.methods) {
                    if (mn.name.equals(fieldNode.name) && mn.desc.equals(fieldNode.desc)) {
                        if (ct.libraryNode) {
                            return true;
                        }
                    }
                }
            }
            for (String parentClass : ct.parentClasses) {
                if (isLibInheritedFN(fieldNode, parentClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Renames the methods in an inheritance tree to prevent inheritance errors.
     *
     * @param visited    a list of {@link ClassTree}s which contain the {@link MethodNode}s we already renamed.
     * @param fieldNode the method information.
     * @param className  the class we are currently browsing through.
     * @param newName    the new name of the method.
     */
    private void renameFieldTree(List<ClassTree> visited, FieldNode fieldNode, String className, String newName) {
        ClassTree ct = hierarchy.get(className);
        if (!ct.libraryNode && !visited.contains(ct)) {
            mappings.put(className + '.' + fieldNode.name, newName);
            visited.add(ct);
            for (String parentClass : ct.parentClasses) {
                renameFieldTree(visited, fieldNode, parentClass, newName);
            }
            for (String subClass : ct.subClasses) {
                renameFieldTree(visited, fieldNode, subClass, newName);
            }
        }
    }
}
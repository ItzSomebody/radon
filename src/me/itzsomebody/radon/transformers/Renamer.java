package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.commons.ClassRemapper;
import me.itzsomebody.radon.asm.commons.Remapper;
import me.itzsomebody.radon.asm.commons.SimpleRemapper;
import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.asm.tree.FieldNode;
import me.itzsomebody.radon.asm.tree.MethodNode;
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
 *
 * @author ItzSomebody
 */
public class Renamer extends AbstractTransformer {
    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

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
        logStrings = new ArrayList<>();
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Generating mappings."));
        long current = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + '.' + methodNode.desc))
                    .filter(methodNode -> !methodNode.name.startsWith("<"))
                    .filter(methodNode -> !BytecodeUtils.isNativeMethod(methodNode.access))
                    .filter(methodNode -> !methodNode.name.equals("main") && !methodNode.name.equals("premain"))
                    .filter(methodNode -> !BytecodeUtils.hasSameMethod(methodNode, classNode, getClassPathMap()))
                    .forEach(methodNode -> {
                        mappings.put(classNode.name + '.' + methodNode.name + methodNode.desc, StringUtils.crazyString());
                    });

            if (classNode.fields != null) classNode.fields.stream()
                    .filter(fieldNode -> !fieldExempted(classNode.name + '.' + fieldNode.name)).forEach(fieldNode -> {
                        mappings.put(classNode.name + '.' + fieldNode.name, StringUtils.crazyString());
                        counter.incrementAndGet();
                    });

            if (!BytecodeUtils.isMain(classNode, spigotMode)) {
                mappings.put(classNode.name, StringUtils.crazyString());
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

    /**
     * Returns {@link String}s to add to log.
     *
     * @return {@link String}s to add to log.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }
}

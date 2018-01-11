package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.asm.tree.FieldNode;
import me.itzsomebody.radon.asm.tree.MethodNode;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies a code hiding technique by applying synthetic modifiers to the class, fields, and methods.
 * Known to have problems with Spigot plugins with EventHandlers.
 *
 * @author ItzSomebody
 */
public class HideCode {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * TODO: Indication to check for EventHandlers before attempting to add synthetic modifier.
     */
    boolean spigotMode;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * Fields protected from obfuscation.
     */
    private ArrayList<String> exemptFields;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link HideCode} object.
     *
     * @param classNode the {@link ClassNode} object to obfuscate.
     * @param spigotMode TODO: indication to check for EventHandlers.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     * @param exemptFields {@link ArrayList} of protected {@link FieldNode}s.
     */
    public HideCode(ClassNode classNode, boolean spigotMode, ArrayList<String> exemptMethods, ArrayList<String> exemptFields) {
        this.classNode = classNode;
        this.spigotMode = spigotMode;
        this.exemptMethods = exemptMethods;
        this.exemptFields = exemptFields;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link HideCode#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting hide code transformer"));
        int count = 0;
        if ((classNode.access & Opcodes.ACC_SYNTHETIC) == 0) {
            classNode.access |= Opcodes.ACC_SYNTHETIC;
            count++;
        }

        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;
            if (!((methodNode.access & Opcodes.ACC_ABSTRACT) == 0)) continue;
            if ((methodNode.access & Opcodes.ACC_SYNTHETIC) == 0) {
                methodNode.access |= Opcodes.ACC_SYNTHETIC;
                count++;
            } // TODO: Fix this from breaking org/bukkit/event/EventHandler
        }

        for (FieldNode fieldNode : classNode.fields) {
            if (exemptFields.contains(classNode.name + "." + fieldNode.name)) continue;
            if ((fieldNode.access & Opcodes.ACC_SYNTHETIC) == 0) {
                fieldNode.access |= Opcodes.ACC_SYNTHETIC;
                count++;
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished hiding code"));
        logStrings.add(LoggerUtils.stdOut("Hid " + String.valueOf(count) + " members"));
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

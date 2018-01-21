package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.MiscUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that adds random GOTO->Label to {@link LightFlowObfuscation#classNode}.
 * This is total trash, consider using it as a last resort if at all.
 *
 * @author ItzSomebody
 */
public class LightFlowObfuscation {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link LightFlowObfuscation} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public LightFlowObfuscation(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link LightFlowObfuscation#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting light flow obfuscation transformer"));
        int addedGotos = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;
            if (methodNode.instructions.size() < 4) continue;

            for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                LabelNode labelNode = new LabelNode();
                if (ain.getOpcode() != Opcodes.GOTO && !(ain instanceof LabelNode)
                        && MiscUtils.getRandomInt(20) < 6) {
                    methodNode.instructions.add(new JumpInsnNode(Opcodes.GOTO, labelNode));
                    methodNode.instructions.add(labelNode);
                    addedGotos++;
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished adding light flow obfuscation"));
        logStrings.add(LoggerUtils.stdOut("Added light flow obfuscation " + String.valueOf(addedGotos) + " times"));
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

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

package me.itzsomebody.radon.transformers.flow;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

/**
 * Transformer that sets GOTO->LABEL instructions as a condition which is always true.
 * <p>
 * getstatic injectedBool (true)
 * iconst_1
 * if_icmpeq LABEL
 * aconst_null
 * athrow
 *
 * @author ItzSomebody
 */
public class LightFlowObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started light flow obfuscation transformer."));
        classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Flow")).forEach(classNode -> {
            String fieldName = StringUtils.randomString(this.dictionary);
            classNode.methods.stream().filter(methodNode -> hasInstructions(methodNode)
                    && !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "Flow")
                    && BytecodeUtils.containsGoto(methodNode)).forEach(methodNode -> {
                for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                    if (this.methodSize(methodNode) > 60000) break;
                    if (ain.getOpcode() == GOTO) {
                        methodNode.instructions.insertBefore(ain, new FieldInsnNode(GETSTATIC, classNode.name, fieldName, "Z"));
                        methodNode.instructions.insertBefore(ain, new InsnNode(ICONST_1));
                        methodNode.instructions.insert(ain, new InsnNode(ATHROW));
                        methodNode.instructions.insert(ain, new InsnNode(ACONST_NULL));
                        methodNode.instructions.set(ain, new JumpInsnNode(IF_ICMPEQ, ((JumpInsnNode) ain).label));
                        counter.incrementAndGet();
                    }
                }
            });
            classNode.fields.add(new FieldNode(ACC_PUBLIC + ACC_STATIC +
                    ACC_FINAL, fieldName, "Z", null, true));
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}

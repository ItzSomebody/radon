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

package me.itzsomebody.radon.transformers.obfuscators.antidebug;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.AccessUtils;
import me.itzsomebody.radon.utils.MethodNodeUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract class for antidebug transformers.
 *
 * @author vovanre
 */
public final class Antidebug extends Transformer {
    private static final List<String> DEBUG_OPTIONS = Arrays.asList("-agentlib:jdwp", "-Xdebug", "-Xrunjdwp:", "-javaagent:");
    private static final List<String> JAVA_AGENT_OPTIONS = Collections.singletonList("-javaagent:");

    protected final AntidebugSetup setup;
    private final List<String> debugOptions;
    private final AtomicInteger debugOptionIndex;

    public Antidebug(AntidebugSetup setup) {
        this.setup = setup;
        this.debugOptions = new ArrayList<>(DEBUG_OPTIONS);
        if (setup.isBlockJavaAgent()) {
            debugOptions.addAll(JAVA_AGENT_OPTIONS);
        }
        this.debugOptionIndex = new AtomicInteger(RandomUtils.getRandomInt(100));
    }

    @Override
    public void transform() {
        this.getClassWrappers().stream()
                .filter(classWrapper -> !AccessUtils.isInterface(classWrapper.classNode.access))
                .forEach(classWrapper -> {
                    MethodNode staticInit = MethodNodeUtils.getOrCreateStaticInit(classWrapper.classNode);

                    int checkCount = RandomUtils.getRandomInt(1, debugOptions.size());
                    for (int i = 0; i < checkCount; i++) {
                        staticInit.instructions.insert(generateCheck());
                    }
                });
    }

    @Override
    public String getName() {
        return "Antidebug";
    }

    private InsnList generateCheck() {
        LabelNode notDebugLabel = new LabelNode();
        InsnList insnList = new InsnList();
        insnList.add(createIsDebugList());
        insnList.add(new JumpInsnNode(Opcodes.IFEQ, notDebugLabel));

        if (RandomUtils.getRandomBoolean()) {
            if (this.setup.getMessage() != null) {
                insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new LdcInsnNode(this.setup.getMessage()));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            }
            if (RandomUtils.getRandomBoolean()) {
                insnList.add(new LdcInsnNode(RandomUtils.getRandomInt()));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));
            } else {
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false));
                insnList.add(new LdcInsnNode(RandomUtils.getRandomInt()));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "halt", "(I)V", false));
            }
        } else {
            String message = this.setup.getMessage();
            if (message == null) {
                message = StringUtils.randomUnrecognizedString(RandomUtils.getRandomInt(5, 15));
            }

            insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/RuntimeException"));
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(new LdcInsnNode(message));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false));
            insnList.add(new InsnNode(Opcodes.ATHROW));
        }
        insnList.add(notDebugLabel);
        return insnList;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return null;
    }

    protected InsnList createIsDebugList() {
        boolean isUpper = RandomUtils.getRandomBoolean();
        String argument = this.debugOptions.get(debugOptionIndex.incrementAndGet() % debugOptions.size());
        if (isUpper) {
            argument = argument.toUpperCase();
        } else {
            argument = argument.toLowerCase();
        }

        InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/management/ManagementFactory", "getRuntimeMXBean", "()Ljava/lang/management/RuntimeMXBean;", false));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/lang/management/RuntimeMXBean", "getInputArguments", "()Ljava/util/List;", true));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", isUpper ? "toUpperCase" : "toLowerCase", "()Ljava/lang/String;", false));
        insnList.add(new LdcInsnNode(argument));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));

        return insnList;
    }
}

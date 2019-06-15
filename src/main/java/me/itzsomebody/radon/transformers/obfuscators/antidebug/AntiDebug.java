/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class AntiDebug extends Transformer {
    private static final String[] DEBUG_OPTIONS = new String[]{"-agentlib:jdwp", "-Xdebug", "-Xrunjdwp:", "-javaagent:"};
    private static final List<String> JAVA_AGENT_OPTIONS = Collections.singletonList("-javaagent:");

    protected final AntidebugSetup setup;
    private final List<String> debugOptions;
    private final AtomicInteger debugOptionIndex;

    private InsnList generateCheck() {
        LabelNode notDebugLabel = new LabelNode();
        InsnList insnList = new InsnList();
        insnList.add(createIsDebugList());
        insnList.add(new JumpInsnNode(IFEQ, notDebugLabel));

        if (RandomUtils.getRandomBoolean()) {
            if (this.setup.getMessage() != null) {
                insnList.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                insnList.add(new LdcInsnNode(this.setup.getMessage()));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            }
            if (RandomUtils.getRandomBoolean()) {
                insnList.add(new LdcInsnNode(RandomUtils.getRandomInt()));
                insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));
            } else {
                insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false));
                insnList.add(new LdcInsnNode(RandomUtils.getRandomInt()));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Runtime", "halt", "(I)V", false));
            }
        } else {
            String message = this.setup.getMessage();
            if (message == null)
                message = randomString();

            insnList.add(new TypeInsnNode(NEW, "java/lang/RuntimeException"));
            insnList.add(new InsnNode(DUP));
            insnList.add(new LdcInsnNode(message));
            insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false));
            insnList.add(new InsnNode(ATHROW));
        }
        insnList.add(notDebugLabel);
        return insnList;
    }

    private InsnList createIsDebugList() {
        boolean isUpper = RandomUtils.getRandomBoolean();
        String argument = this.debugOptions.get(debugOptionIndex.incrementAndGet() % debugOptions.size());
        if (isUpper) {
            argument = argument.toUpperCase();
        } else {
            argument = argument.toLowerCase();
        }

        InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/management/ManagementFactory", "getRuntimeMXBean", "()Ljava/lang/management/RuntimeMXBean;", false));
        insnList.add(new MethodInsnNode(INVOKEINTERFACE, "java/lang/management/RuntimeMXBean", "getInputArguments", "()Ljava/util/List;", true));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", isUpper ? "toUpperCase" : "toLowerCase", "()Ljava/lang/String;", false));
        insnList.add(new LdcInsnNode(argument));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false));

        return insnList;
    }

    @Override
    public void transform() {
        getClassWrappers().stream().filter(cw -> !cw.getAccess().isInterface()
                && !excluded(cw)).forEach(cw -> cw.getMethods().stream().filter(mw -> !excluded(mw)).forEach(mw -> {

        }));
    }

    @Override
    public String getName() {
        return "Anti-Debug";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.ANTI_DEBUG;
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // Not needed
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.ANTI_DEBUG + " expects a boolean");
    }
}

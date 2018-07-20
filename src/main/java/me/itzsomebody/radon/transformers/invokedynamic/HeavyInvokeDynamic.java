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

package me.itzsomebody.radon.transformers.invokedynamic;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.classes.InvokeDynamicBootstrap;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * Transformer that applies an InvokeDynamic obfuscation to field and
 * (virtual and static) method access.
 *
 * @author ItzSomebody.
 */
public class HeavyInvokeDynamic extends LightInvokeDynamic {
    /**
     * Applies obfuscation.
     */
    @Override
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        MemberNames memberNames = new MemberNames(this);
        ArrayList<String> finals = new ArrayList<>();
        this.classNodes().forEach(classNode ->
                classNode.fields.stream().filter(fieldNode -> Modifier.isFinal(fieldNode.access)).forEach(fieldNode ->
                        finals.add(classNode.name + '.' + fieldNode.name)
                )
        );
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started heavy invokedynamic transformer"));
        Handle bsmHandle = new Handle(H_INVOKESTATIC, memberNames.className, memberNames.bootstrapMethodName, "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        this.classNodes().parallelStream().filter(classNode -> !this.exempted(classNode.name, "InvokeDynamic")
                && classNode.version >= 51).forEach(classNode ->
                classNode.methods.parallelStream().filter(methodNode ->
                        !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "InvokeDynamic")
                                && hasInstructions(methodNode)).forEach(methodNode -> {
                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (this.methodSize(methodNode) > 60000) break;
                        if (insn instanceof MethodInsnNode) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                            if (!methodInsnNode.name.equals("<init>")) {
                                boolean isStatic = (methodInsnNode.getOpcode() == INVOKESTATIC);
                                String newSig = isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(", "(Ljava/lang/Object;");
                                Type returnType = Type.getReturnType(methodInsnNode.desc);
                                Type[] args = Type.getArgumentTypes(newSig);
                                for (int i = 0; i < args.length; i++) {
                                    Type arg = args[i];
                                    if (arg.getSort() == Type.OBJECT) {
                                        args[i] = Type.getType("Ljava/lang/Object;");
                                    }
                                }
                                newSig = Type.getMethodDescriptor(returnType, args);
                                StringBuilder sb = new StringBuilder();
                                sb.append(methodInsnNode.owner.replace("/", ".")).append("<>").append(methodInsnNode.name).append("<>");

                                switch (insn.getOpcode()) {
                                    case INVOKEINTERFACE:
                                    case INVOKEVIRTUAL: {
                                        sb.append("1<>").append(methodInsnNode.desc);
                                        break;
                                    }
                                    case INVOKESPECIAL: {
                                        sb.append("2<>").append(methodInsnNode.desc).append("<>").append(classNode.name.replace("/", "."));
                                        break;
                                    }
                                    case INVOKESTATIC: {
                                        sb.append("0<>").append(methodInsnNode.desc);
                                        break;
                                    }
                                }

                                InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                        encrypt(sb.toString(), memberNames),
                                        newSig,
                                        bsmHandle
                                );

                                methodNode.instructions.set(insn, indy);
                                if (returnType.getSort() == Type.ARRAY) {
                                    methodNode.instructions.insert(indy, new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
                                }
                                counter.incrementAndGet();
                            }
                        } else if (insn instanceof FieldInsnNode) {
                            if (!methodNode.name.equals("<init>")) {
                                FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;

                                if (finals.contains(fieldInsnNode.owner + '.' + fieldInsnNode.name)) {
                                    continue;
                                }

                                boolean isStatic = (fieldInsnNode.getOpcode() == GETSTATIC || fieldInsnNode.getOpcode() == PUTSTATIC);
                                boolean isSetter = (fieldInsnNode.getOpcode() == PUTFIELD || fieldInsnNode.getOpcode() == PUTSTATIC);
                                String newSig = (isSetter) ? "(" + fieldInsnNode.desc + ")V" : "()" + fieldInsnNode.desc;
                                if (!isStatic)
                                    newSig = newSig.replace("(", "(Ljava/lang/Object;");

                                StringBuilder sb = new StringBuilder();
                                sb.append(fieldInsnNode.owner.replace("/", ".")).append("<>").append(fieldInsnNode.name).append("<>");

                                switch (insn.getOpcode()) {
                                    case GETSTATIC: {
                                        sb.append("3");
                                        break;
                                    }
                                    case GETFIELD: {
                                        sb.append("4");
                                        break;
                                    }
                                    case PUTSTATIC: {
                                        sb.append("5");
                                        break;
                                    }
                                    case PUTFIELD: {
                                        sb.append("6");
                                        break;
                                    }
                                }

                                InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                        encrypt(sb.toString(), memberNames),
                                        newSig,
                                        bsmHandle
                                );

                                methodNode.instructions.set(insn, indy);
                                counter.incrementAndGet();
                            }
                        }
                    }
                })
        );

        ClassNode decryptor = InvokeDynamicBootstrap.heavyBootstrap(memberNames);
        this.getClassMap().put(decryptor.name, decryptor);
        this.logStrings.add(LoggerUtils.stdOut("Hid " + counter + " field and/or method accesses with invokedynamics."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    private static String encrypt(String msg, MemberNames memberNames) {
        char[] chars = msg.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            switch (i % 4) {
                case 0: {
                    sb.append((char) (chars[i] ^ memberNames.className.replace("/", ".").hashCode()));
                    break;
                }
                case 1: {
                    sb.append((char) (chars[i] ^ memberNames.bootstrapMethodName.hashCode()));
                    break;
                }
                case 2: {
                    sb.append((char) (chars[i] ^ memberNames.className.replace("/", ".").hashCode()));
                    break;
                }
                case 3: {
                    sb.append((char) (chars[i] ^ memberNames.decryptorMethodName.hashCode()));
                    break;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Names of bootstrap class and members.
     */
    public class MemberNames {
        public String className;
        public String decryptorMethodName;
        public String bootstrapMethodName;
        public String searchMethodName;

        MemberNames(HeavyInvokeDynamic instance) {
            this.className = StringUtils.randomClassName(instance.classNames(), instance.dictionary, len);
            this.decryptorMethodName = StringUtils.randomString(instance.dictionary, len);
            this.bootstrapMethodName = StringUtils.randomString(instance.dictionary, len);
            this.searchMethodName = StringUtils.randomString(instance.dictionary, len);
        }
    }
}
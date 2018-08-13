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

package me.itzsomebody.radon.transformers.miscellaneous.expiration;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class Expiration extends Transformer {
    private ExpirationSetup setup;

    public Expiration(ExpirationSetup setup) {
        this.setup = setup;
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            classNode.methods.stream().filter(methodNode -> methodNode.name.equals("<init>")).forEach(methodNode -> {
                InsnList expirationCode = createExpiration();
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), expirationCode);
                counter.incrementAndGet();
            });
        });

        LoggerUtils.stdOut(String.format("Added %d expiration code blocks.", counter.get()));
    }

    private InsnList createExpiration() {
        InsnList expiryCode = new InsnList();
        LabelNode injectedLabel = new LabelNode(new Label());

        expiryCode.add(new TypeInsnNode(NEW, "java/util/Date"));
        expiryCode.add(new InsnNode(DUP));
        expiryCode.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
        expiryCode.add(new TypeInsnNode(NEW, "java/util/Date"));
        expiryCode.add(new InsnNode(DUP));
        expiryCode.add(new LdcInsnNode(this.setup.getExpires()));
        expiryCode.add(new MethodInsnNode(INVOKESPECIAL, "java/util/Date", "<init>", "(J)V", false));
        expiryCode.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Date", "after", "(Ljava/util/Date;)Z", false));
        expiryCode.add(new JumpInsnNode(IFEQ, injectedLabel));
        expiryCode.add(new TypeInsnNode(NEW, "java/lang/Throwable"));
        expiryCode.add(new InsnNode(DUP));
        expiryCode.add(new LdcInsnNode(this.setup.getMessage()));
        if (this.setup.isInjectJOptionPane()) {
            expiryCode.add(new InsnNode(DUP));
            expiryCode.add(new InsnNode(ACONST_NULL));
            expiryCode.add(new InsnNode(SWAP));
            expiryCode.add(new MethodInsnNode(INVOKESTATIC, "javax/swing/JOptionPane", "showMessageDialog", "(Ljava/awt/Component;Ljava/lang/Object;)V", false));
        }
        expiryCode.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        expiryCode.add(new InsnNode(ATHROW));
        expiryCode.add(injectedLabel);

        return expiryCode;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.EXPIRATION;
    }

    @Override
    public String getName() {
        return "Expiration";
    }

    public ExpirationSetup getSetup() {
        return setup;
    }
}

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

package me.itzsomebody.radon.transformers.obfuscators.flow;

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * Traps random instructions using a fake handler. Essentially the same thing as Zelix's exception obfuscation
 * or Dasho's fake try catches.
 *
 * @author ItzSomebody
 */
public class FakeCatchBlocks extends FlowObfuscation {
    private static final String[] HANDLER_NAMES = {
            RuntimeException.class.getName().replace('.', '/'),
            LinkageError.class.getName().replace('.', '/'),
            Error.class.getName().replace('.', '/'),
            Exception.class.getName().replace('.', '/'),
            Throwable.class.getName().replace('.', '/'),
            IllegalArgumentException.class.getName().replace('.', '/'),
            IllegalStateException.class.getName().replace('.', '/'),
            IllegalAccessError.class.getName().replace('.', '/'),
            InvocationTargetException.class.getName().replace('.', '/'),
            IOException.class.getName().replace('.', '/'),
            IOError.class.getName().replace('.', '/'),
    };

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        ClassNode fakeHandler = new ClassNode();
        fakeHandler.superName = HANDLER_NAMES[RandomUtils.getRandomInt(HANDLER_NAMES.length)];
        fakeHandler.name = uniqueRandomString();
        fakeHandler.access = ACC_PUBLIC | ACC_SUPER;
        fakeHandler.version = V1_5;

        String methodName = uniqueRandomString();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(mw -> !excluded(mw) && hasInstructions(mw.getMethodNode())
                        && !"<init>".equals(mw.getOriginalName())).forEach(methodWrapper -> {
                    int leeway = getSizeLeeway(methodWrapper);
                    InsnList insns = methodWrapper.getInstructions();

                    for (AbstractInsnNode insn : insns.toArray()) {
                        if (leeway < 10000)
                            return;
                        if (!ASMUtils.isInstruction(insn))
                            continue;

                        if (RandomUtils.getRandomInt(10) > 6) {
                            LabelNode trapStart = new LabelNode();
                            LabelNode trapEnd = new LabelNode();
                            LabelNode catchStart = new LabelNode();
                            LabelNode catchEnd = new LabelNode();

                            InsnList catchBlock = new InsnList();
                            catchBlock.add(catchStart);
                            catchBlock.add(new InsnNode(DUP));
                            catchBlock.add(new MethodInsnNode(INVOKEVIRTUAL, fakeHandler.name, methodName, "()V", false));
                            catchBlock.add(new InsnNode(ATHROW));
                            catchBlock.add(catchEnd);

                            insns.insertBefore(insn, trapStart);
                            insns.insert(insn, catchBlock);
                            insns.insert(insn, new JumpInsnNode(GOTO, catchEnd));
                            insns.insert(insn, trapEnd);

                            methodWrapper.getTryCatchBlocks().add(new TryCatchBlockNode(trapStart, trapEnd, catchStart, fakeHandler.name));

                            leeway -= 15;
                            counter.incrementAndGet();
                        }
                    }
                }));
        ClassWrapper newWrapper = new ClassWrapper(fakeHandler, false);
        getClasses().put(fakeHandler.name, newWrapper);
        getClassPath().put(fakeHandler.name, newWrapper);

        Main.info("Inserted " + counter.get() + " fake try catches");
    }
}

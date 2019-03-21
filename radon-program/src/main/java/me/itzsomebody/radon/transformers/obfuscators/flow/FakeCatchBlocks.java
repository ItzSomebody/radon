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
import java.util.stream.Stream;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
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

        int fakeHandlerClasses = getClassWrappers().size() / 5;
        if (fakeHandlerClasses == 0)
            fakeHandlerClasses = 1;

        ClassNode[] fakeHandlers = new ClassNode[fakeHandlerClasses];
        for (int i = 0; i < fakeHandlerClasses; i++) {
            ClassNode classNode = new ClassNode();
            classNode.superName = HANDLER_NAMES[RandomUtils.getRandomInt(HANDLER_NAMES.length)];
            classNode.name = randomString();
            classNode.access = ACC_PUBLIC | ACC_SUPER;

            fakeHandlers[i] = classNode;
        }

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;

                    int leeway = getSizeLeeway(methodNode);

                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (leeway < 10000)
                            return;

                        if (RandomUtils.getRandomInt(10) > 5) {
                            LabelNode trapStart = new LabelNode();
                            LabelNode trapEnd = new LabelNode();
                            LabelNode catchStart = new LabelNode();
                            LabelNode catchEnd = new LabelNode();

                            InsnList catchBlock = new InsnList();
                            catchBlock.add(catchStart);
                            catchBlock.add(new InsnNode(DUP));
                            catchBlock.add(new InsnNode(POP));
                            catchBlock.add(new InsnNode(ATHROW));
                            catchBlock.add(catchEnd);

                            methodNode.instructions.insertBefore(insn, trapStart);
                            methodNode.instructions.insert(insn, trapEnd);
                            methodNode.instructions.insert(insn, new JumpInsnNode(GOTO, catchEnd));
                            methodNode.instructions.insert(insn, catchBlock);

                            methodNode.tryCatchBlocks.add(new TryCatchBlockNode(trapStart, trapEnd, catchStart,
                                    fakeHandlers[RandomUtils.getRandomInt(fakeHandlers.length)].name));

                            leeway -= 15;
                            counter.incrementAndGet();
                        }
                    }
                }));

        Stream.of(fakeHandlers).forEach(classNode -> getClasses().put(classNode.name, new ClassWrapper(classNode, false)));

        Logger.stdOut("Created " + fakeHandlerClasses + " fake handler classes");
        Logger.stdOut("Inserted " + counter.get() + " fake try catches");
    }
}

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

package me.itzsomebody.radon.transformers.miscellaneous;

import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TrashClasses extends Transformer {
    @Override
    public void transform() {
        for (int i = 0; i < this.radon.sessionInfo.getTrashClasses(); i++) {
            ClassNode classNode = generateClass();
            ClassWriter cw = new ClassWriter(0);
            cw.newUTF8("RADON" + Main.VERSION);
            classNode.accept(cw);

            this.getResources().put(classNode.name, cw.toByteArray());
        }

        LoggerUtils.stdOut(String.format("Generated %d trash classes.", this.radon.sessionInfo.getTrashClasses()));
    }

    private ClassNode generateClass() {
        ClassNode classNode = createClass(StringUtils.randomClassName(this.getClasses().keySet()));
        int methodsToGenerate = RandomUtils.getRandomInt(3) + 2;

        for (int i = 0; i < methodsToGenerate; i++) {
            classNode.methods.add(methodGen());
        }

        return classNode;
    }

    private ClassNode createClass(String className) {
        ClassNode classNode = new ClassNode();
        classNode.visit(49, ACC_SUPER + ACC_PUBLIC, className, null, "java/lang/Object", null);

        MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        classNode.visitEnd();

        return classNode;
    }

    private MethodNode methodGen() {
        String randDesc = descGen();
        MethodNode method = new MethodNode(ACC_STATIC + ACC_PRIVATE, StringUtils.randomSpacesString(8), randDesc, null, null);
        int instructions = RandomUtils.getRandomInt(30) + 30;

        InsnList insns = new InsnList();

        for (int i = 0; i < instructions; ++i) {
            insns.add(junkInsns());
        }

        if (randDesc.endsWith(")Ljava/lang/String;")
            || randDesc.endsWith(")Ljava/lang/Object;")) {
            insns.add(new VarInsnNode(ALOAD,
                RandomUtils.getRandomInt(30)));
            insns.add(new InsnNode(ARETURN));
        } else if (randDesc.endsWith(")Z")) {
            if (RandomUtils.getRandomInt(1) == 1) {
                insns.add(new InsnNode(ICONST_0));
            } else {
                insns.add(new InsnNode(ICONST_1));
            }

            insns.add(new InsnNode(IRETURN));
        } else if (randDesc.endsWith(")V")) {
            insns.add(new InsnNode(RETURN));
        }

        method.instructions = insns;
        return method;
    }

    private String descGen() {
        switch (RandomUtils.getRandomInt(7)) {
            case 0:
                return "(Ljava/lang/String;)Ljava/lang/String;";
            case 1:
                return "(Ljava/lang/Object;)Ljava/lang/Object;";
            case 2:
                return "(I)Z";
            case 3:
                return "()V";
            case 4:
                return "(B)V";
            case 5:
                return "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
            case 6: // False BSM lol
            default:
                return "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;";
        }
    }

    private static AbstractInsnNode junkInsns() {
        int index = RandomUtils.getRandomInt(20);
        switch (index) {
            case 0:
                return new MethodInsnNode(INVOKESTATIC, StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), "(Ljava/lang/String;)V", false);
            case 1:
                return new FieldInsnNode(GETFIELD, StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), "I");
            case 2:
                return new InsnNode(RandomUtils.getRandomInt(16));
            case 3:
                return new VarInsnNode(ALOAD, RandomUtils.getRandomInt(30));
            case 4:
                return new IntInsnNode(BIPUSH, RandomUtils.getRandomInt(255));
            case 5:
                return new IntInsnNode(SIPUSH, RandomUtils.getRandomInt(25565));
            case 6:
            case 7:
            case 8:
                return new InsnNode(RandomUtils.getRandomInt(5));
            case 9:
                return new LdcInsnNode(StringUtils.randomSpacesString(8));
            case 10:
                return new IincInsnNode(RandomUtils.getRandomInt(16), RandomUtils.getRandomInt(16));
            case 11:
                return new MethodInsnNode(INVOKESPECIAL, StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), "()V", false);
            case 12:
                return new MethodInsnNode(INVOKEVIRTUAL, StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            case 13:
                return new VarInsnNode(ILOAD, RandomUtils.getRandomInt(30));
            case 14:
                return new InsnNode(ATHROW);
            case 15:
                return new MethodInsnNode(INVOKEINTERFACE, StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), "(I)I", false);
            case 16:
                Handle handle = new Handle(6, StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), false);
                return new InvokeDynamicInsnNode(StringUtils.randomSpacesString(8), StringUtils.randomSpacesString(8), handle, RandomUtils.getRandomInt(5), RandomUtils.getRandomInt(5), RandomUtils.getRandomInt(5), RandomUtils.getRandomInt(5), RandomUtils.getRandomInt(5));
            case 17:
                return new IntInsnNode(ANEWARRAY, RandomUtils.getRandomInt(30));
            case 18:
                return new VarInsnNode(ASTORE, RandomUtils.getRandomInt(30));
            case 19:
            default:
                return new VarInsnNode(ISTORE, RandomUtils.getRandomInt(30));
        }
    }

    @Override
    protected ExclusionType getExclusionType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}

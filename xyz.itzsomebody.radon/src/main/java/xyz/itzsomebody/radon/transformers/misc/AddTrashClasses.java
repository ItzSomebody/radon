/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.dictionaries.DictionaryFactory;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.RandomUtils;
import xyz.itzsomebody.radon.utils.asm.ASMUtils;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.ArrayList;

/**
 * Not really a transformer. This "transformer" generates unused classes full
 * of random bytecode. This is copy-pasted from radon 2 -- no need to rewrite.
 *
 * @author ItzSomebody
 */
public class AddTrashClasses extends Transformer {
    private static ArrayList<String> DESCRIPTORS = new ArrayList<>();

    @JsonProperty("dictionary")
    private Dictionary dictionary = DictionaryFactory.defaultDictionary();

    @JsonProperty("number_of_trash_classes")
    private int nTrashClasses;

    static {
        DESCRIPTORS.add("Z");
        DESCRIPTORS.add("C");
        DESCRIPTORS.add("B");
        DESCRIPTORS.add("S");
        DESCRIPTORS.add("I");
        DESCRIPTORS.add("F");
        DESCRIPTORS.add("J");
        DESCRIPTORS.add("D");
        DESCRIPTORS.add("V");
    }

    @Override
    public void transform() {
        ArrayList<String> classNames = new ArrayList<>(classPathMap().keySet());
        for (int i = 0; i < classNames.size() % 20; i++) {
            DESCRIPTORS.add("L" + classNames.get(RandomUtils.randomInt(classNames.size())) + ";");
        }

        for (int i = 0; i < nTrashClasses; i++) {
            var classNode = generateClass();
            var cw = new ClassWriter(0);
            cw.newUTF8("RADON" + RadonConstants.VERSION);
            classNode.accept(cw);

            resourceMap().put(classNode.name + ".class", cw.toByteArray());
        }

        RadonLogger.info(String.format("Generated %d trash classes.", nTrashClasses));
    }

    private ClassNode generateClass() {
        ClassNode classNode = createClass(fakeSubClass());
        int methodsToGenerate = RandomUtils.randomInt(3) + 2;

        for (int i = 0; i < methodsToGenerate; i++)
            classNode.methods.add(methodGen());

        return classNode;
    }

    private ClassNode createClass(String className) {
        ClassNode classNode = new ClassNode();
        classNode.visit(49, ACC_SUPER + ACC_PUBLIC, className, null, "java/lang/Object", null);

        var mv = classNode.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
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
        MethodNode method = new MethodNode(ACC_STATIC + ACC_PRIVATE, dictionary.next(), randDesc, null, null);
        int instructions = RandomUtils.randomInt(30) + 30;

        InsnList insns = new InsnList();

        for (int i = 0; i < instructions; ++i) {
            insns.add(junkInstructions());
        }

        Type returnType = Type.getReturnType(randDesc);
        switch (returnType.getSort()) {
            case Type.VOID:
                insns.add(new InsnNode(RETURN));
                break;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                if (RandomUtils.randomInt(10) % 2 == 1)
                    insns.add(new InsnNode(ICONST_0));
                else
                    insns.add(new InsnNode(ICONST_1));

                insns.add(new InsnNode(IRETURN));
                break;
            case Type.FLOAT:
                insns.add(ASMUtils.getNumberInsn(RandomUtils.randomFloat()));
                insns.add(new InsnNode(FRETURN));
                break;
            case Type.LONG:
                insns.add(ASMUtils.getNumberInsn(RandomUtils.randomLong()));
                insns.add(new InsnNode(LRETURN));
                break;
            case Type.DOUBLE:
                insns.add(ASMUtils.getNumberInsn(RandomUtils.randomDouble()));
                insns.add(new InsnNode(DRETURN));
                break;
            default:
                insns.add(new VarInsnNode(ALOAD, RandomUtils.randomInt(30)));
                insns.add(new InsnNode(ARETURN));
                break;
        }

        method.instructions = insns;
        return method;
    }

    private String descGen() {
        StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < RandomUtils.randomInt(7); i++)
            sb.append(DESCRIPTORS.get(RandomUtils.randomInt(DESCRIPTORS.size())));

        sb.append(")");
        sb.append(DESCRIPTORS.get(RandomUtils.randomInt(DESCRIPTORS.size())));

        return sb.toString();
    }

    private AbstractInsnNode junkInstructions() {
        int index = RandomUtils.randomInt(20);
        switch (index) {
            case 0:
                return new MethodInsnNode(INVOKESTATIC, dictionary.next(), dictionary.next(), "(Ljava/lang/String;)V", false);
            case 1:
                return new FieldInsnNode(GETFIELD, dictionary.next(), dictionary.next(), "I");
            case 2:
                return new InsnNode(RandomUtils.randomInt(16));
            case 3:
                return new VarInsnNode(ALOAD, RandomUtils.randomInt(30));
            case 4:
                return new IntInsnNode(BIPUSH, RandomUtils.randomInt(255));
            case 5:
                return new IntInsnNode(SIPUSH, RandomUtils.randomInt(25565));
            case 6:
            case 7:
            case 8:
                return new InsnNode(RandomUtils.randomInt(5));
            case 9:
                return new LdcInsnNode(dictionary.next());
            case 10:
                return new IincInsnNode(RandomUtils.randomInt(16), RandomUtils.randomInt(16));
            case 11:
                return new MethodInsnNode(INVOKESPECIAL, dictionary.next(), dictionary.next(), "()V", false);
            case 12:
                return new MethodInsnNode(INVOKEVIRTUAL, dictionary.next(), dictionary.next(), "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            case 13:
                return new VarInsnNode(ILOAD, RandomUtils.randomInt(30));
            case 14:
                return new InsnNode(ATHROW);
            case 15:
                return new MethodInsnNode(INVOKEINTERFACE, dictionary.next(), dictionary.next(), "(I)I", false);
            case 16:
                Handle handle = new Handle(6, dictionary.next(), dictionary.next(), dictionary.next(), false);
                return new InvokeDynamicInsnNode(dictionary.next(), dictionary.next(), handle, RandomUtils.randomInt(5), RandomUtils.randomInt(5), RandomUtils.randomInt(5), RandomUtils.randomInt(5), RandomUtils.randomInt(5));
            case 17:
                return new IntInsnNode(ANEWARRAY, RandomUtils.randomInt(30));
            case 18:
                return new VarInsnNode(ASTORE, RandomUtils.randomInt(30));
            case 19:
            default:
                return new VarInsnNode(ISTORE, RandomUtils.randomInt(30));
        }
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.ADD_TRASH_CLASSES;
    }

    @Override
    public String getConfigName() {
        return Transformers.ADD_TRASH_CLASSES.getConfigName();
    }
}

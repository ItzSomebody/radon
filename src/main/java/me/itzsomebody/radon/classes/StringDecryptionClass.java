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


package me.itzsomebody.radon.classes;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class StringDecryptionClass implements Opcodes {
    public static ClassNode getHeavyDecrypt(String className, String decryptorMethodName,
                                            String cacheFieldName, String key1FieldName,
                                            String key2FieldName, String hashMethodName,
                                            String hashGetterMethodName, String hashSetterMethodName) {

        ClassNode cw = new ClassNode();
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, cacheFieldName, "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/Integer;Ljava/lang/String;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, key1FieldName, "I", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, key2FieldName, "I", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, className, cacheFieldName, "Ljava/util/HashMap;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 0);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitFieldInsn(PUTSTATIC, className, key1FieldName, "I");
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitFieldInsn(PUTSTATIC, className, key2FieldName, "I");
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, hashMethodName, "([C)I", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ISTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ISTORE, 3);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 4);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 5);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 6);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 7);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitIntInsn(BIPUSH, 6);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 8);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 4);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 6);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IUSHR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitIntInsn(BIPUSH, 6);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 5);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ISTORE, 7);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_5);
            mv.visitInsn(IUSHR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 8);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(IXOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 1);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitIincInsn(2, 1);
            mv.visitLabel(l3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l4);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IRETURN);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitMaxs(4, 9);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, hashGetterMethodName, "(I)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, className, cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ILOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, hashSetterMethodName, "(Ljava/lang/String;I)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, className, cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, decryptorMethodName, "(Ljava/lang/Object;Ljava/lang/Object;I)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, className, hashMethodName, "([C)I", false);
            mv.visitVarInsn(ISTORE, 4);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, className, hashGetterMethodName, "(I)Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 5);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 5);
            Label l4 = new Label();
            mv.visitJumpInsn(IFNULL, l4);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 6);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 7);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 8);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 10);
            Label l10 = new Label();
            mv.visitLabel(l10);
            Label l11 = new Label();
            mv.visitJumpInsn(GOTO, l11);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(IREM);
            Label l13 = new Label();
            Label l14 = new Label();
            Label l15 = new Label();
            Label l16 = new Label();
            Label l17 = new Label();
            Label l18 = new Label();
            Label l19 = new Label();
            Label l20 = new Label();
            Label l21 = new Label();
            mv.visitTableSwitchInsn(0, 7, l21, l13, l14, l15, l16, l17, l18, l19, l20);
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l14);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitFieldInsn(GETSTATIC, className, key2FieldName, "I");
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitFieldInsn(GETSTATIC, className, key1FieldName, "I");
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l24 = new Label();
            mv.visitLabel(l24);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitFieldInsn(GETSTATIC, className, key2FieldName, "I");
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l25 = new Label();
            mv.visitLabel(l25);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l17);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitFieldInsn(GETSTATIC, className, key1FieldName, "I");
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l26 = new Label();
            mv.visitLabel(l26);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitFieldInsn(GETSTATIC, className, key2FieldName, "I");
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l27 = new Label();
            mv.visitLabel(l27);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l19);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l21);
            mv.visitIincInsn(10, 1);
            mv.visitLabel(l11);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l12);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 10);
            Label l30 = new Label();
            mv.visitLabel(l30);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, className, hashSetterMethodName, "(Ljava/lang/String;I)V", false);
            Label l31 = new Label();
            mv.visitLabel(l31);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitInsn(ARETURN);
            Label l32 = new Label();
            mv.visitLabel(l32);
            mv.visitMaxs(4, 11);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }
}
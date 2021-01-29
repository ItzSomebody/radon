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

package xyz.itzsomebody.commons;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

/**
 * Determines the numbers of locals slots being used by a method based on the variable instructions within the method.
 *
 * @author itzsomebody
 */
public class MaxLocalsUpdater {
    private final MethodNode methodNode;

    private MaxLocalsUpdater(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    private int computeMaxs() {
        int size = 0;
        if ((methodNode.access & Opcodes.ACC_STATIC) == 0) {
            size += 1; // Non-static implicitly declares variable 0 as "this"
        }
        for (var type : Type.getArgumentTypes(methodNode.desc)) {
            size += type.getSize();
        }
        var visitor = new MaxLocalsVisitor(size);
        methodNode.accept(visitor);
        return visitor.getSize(); // Quality code /s
    }

    public static void update(MethodNode methodNode) {
        methodNode.visitMaxs(methodNode.maxStack, new MaxLocalsUpdater(methodNode).computeMaxs());
    }

    class MaxLocalsVisitor extends MethodVisitor {
        private int size;

        public MaxLocalsVisitor(int initialSize) {
            super(/* latest api */ Opcodes.ASM9, null);
            this.size = initialSize;
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (var >= size) {
                size = var + 1;
                if (opcode == Opcodes.LLOAD
                        || opcode == Opcodes.DLOAD
                        || opcode == Opcodes.LSTORE
                        || opcode == Opcodes.DSTORE) {
                    size += 1;
                }
            }
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            if (var >= size) {
                size = var + 1;
            }
        }

        public int getSize() {
            return size;
        }
    }
}

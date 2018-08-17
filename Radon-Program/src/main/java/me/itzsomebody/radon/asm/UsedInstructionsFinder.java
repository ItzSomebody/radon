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

package me.itzsomebody.radon.asm;

import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

/**
 * Runs through all possible execution flows of a set of instructions to figure
 * what instructions are used.
 *
 * @author ItzSomebody
 */
public class UsedInstructionsFinder {
    private InsnList instructions;
    private Set<AbstractInsnNode> usedInstructions = new HashSet<>();

    public UsedInstructionsFinder(InsnList instructions) {
        this.instructions = instructions;
    }

    public Set<AbstractInsnNode> getUsedInstructions() {
        execute(instructions.getFirst());
        return usedInstructions;
    }

    private void execute(AbstractInsnNode insn) {
        while (true) {
            if (insn == null || usedInstructions.contains(insn)) {
                return;
            }

            usedInstructions.add(insn);
            if (insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN) {
                return;
            } else if (insn.getOpcode() >= Opcodes.IFEQ && insn.getOpcode() <= Opcodes.GOTO) {
                execute(((JumpInsnNode) insn).label);
            } else if (insn.getOpcode() == Opcodes.JSR || insn.getOpcode() == Opcodes.RET) {
                throw new RuntimeException("Did not expect JSR/RET instruction");
            }

            insn = insn.getNext();
        }
    }
}
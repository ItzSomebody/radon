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

package xyz.itzsomebody.commons.matcher.rules;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;

public class IntConstRule extends OpcodeRule {
    public IntConstRule() {
        super(current -> {
                    if (current instanceof LdcInsnNode && !(((LdcInsnNode) current).cst instanceof Integer)) {
                        return false;
                    }
                    return true;
                }, Opcodes.ICONST_M1,
                Opcodes.ICONST_0,
                Opcodes.ICONST_1,
                Opcodes.ICONST_2,
                Opcodes.ICONST_3,
                Opcodes.ICONST_4,
                Opcodes.ICONST_5,
                Opcodes.BIPUSH,
                Opcodes.SIPUSH,
                Opcodes.LDC
        );
    }
}

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

public class DoubleConstRule extends OpcodeRule {
    public DoubleConstRule() {
        super(current -> {
                    if (current instanceof LdcInsnNode && !(((LdcInsnNode) current).cst instanceof Double)) {
                        return false;
                    }
                    return true;
                }, Opcodes.DCONST_0,
                Opcodes.DCONST_1,
                Opcodes.LDC
        );
    }
}

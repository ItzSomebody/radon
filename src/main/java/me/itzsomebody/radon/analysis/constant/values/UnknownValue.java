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

package me.itzsomebody.radon.analysis.constant.values;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

public final class UnknownValue extends AbstractValue {
    public static AbstractValue UNINITIALIZED_VALUE = new UnknownValue(null, null);

    public UnknownValue(Type type) {
        super(null, type);
    }

    public UnknownValue(AbstractInsnNode insnNode, Type type) {
        super(insnNode, type);
    }

    @Override
    public boolean isConstant() {
        return false;
    }
}

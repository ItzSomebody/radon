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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Value;

public abstract class AbstractValue implements Value {
    private final AbstractInsnNode insnNode;
    private final Type type;
    private final Set<AbstractInsnNode> usages;

    AbstractValue(AbstractInsnNode insnNode, Type type) {
        this.insnNode = insnNode;
        this.type = type;
        usages = new HashSet<>();
    }

    private static <E> boolean containsAll(final Set<E> self, final Set<E> other) {
        if (self.size() < other.size()) {
            return false;
        }
        return self.containsAll(other);
    }

    public final Set<AbstractInsnNode> getUsages() {
        return Collections.unmodifiableSet(usages);
    }

    public final void addUsage(AbstractInsnNode insnNode) {
        usages.add(insnNode);
    }

    public final int getSize() {
        return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
    }

    public abstract boolean isConstant();

    public AbstractInsnNode getInsnNode() {
        return insnNode;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractValue)) return false;
        AbstractValue that = (AbstractValue) o;
        return Objects.equals(insnNode, that.insnNode) &&
                Objects.equals(type, that.type) &&
                containsAll(usages, that.usages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insnNode, type, usages);
    }
}

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

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows for easy modifications of {@link org.objectweb.asm.tree.InsnList}s. This is identical in idea to
 * the "InstructionModifier" in java-deobfuscator. The idea of this modifier is to make it easier to modify instruction
 * lists while iterating over them.
 */
public class InsnListModifier {
    private final List<InsnList> appends = new ArrayList<>();
    private final List<InsnList> prepends = new ArrayList<>();
    private final Map<AbstractInsnNode, InsnList> inserts = new HashMap<>();
    private final Map<AbstractInsnNode, InsnList> insertBefores = new HashMap<>();
    private final Map<AbstractInsnNode, InsnList> replacements = new HashMap<>();
    private final List<AbstractInsnNode> removals = new ArrayList<>();

    public InsnListModifier append(InsnList insns) {
        appends.add(insns);
        return this;
    }

    public InsnListModifier append(@NotNull AbstractInsnNode... insns) {
        var list = new InsnList();
        List.of(insns).forEach(list::add);
        appends.add(list);
        return this;
    }

    public InsnListModifier prepend(InsnList insns) {
        prepends.add(insns);
        return this;
    }

    public InsnListModifier prepend(@NotNull AbstractInsnNode... insns) {
        var list = new InsnList();
        List.of(insns).forEach(list::add);
        prepends.add(list);
        return this;
    }

    public InsnListModifier insert(AbstractInsnNode previous, InsnList insns) {
        inserts.put(previous, insns);
        return this;
    }

    public InsnListModifier insert(AbstractInsnNode previous, @NotNull AbstractInsnNode... insns) {
        var list = new InsnList();
        List.of(insns).forEach(list::add);
        inserts.put(previous, list);
        return this;
    }

    public InsnListModifier insertBefore(AbstractInsnNode next, InsnList insns) {
        insertBefores.put(next, insns);
        return this;
    }

    public InsnListModifier insertBefore(AbstractInsnNode next, @NotNull AbstractInsnNode... insns) {
        var list = new InsnList();
        List.of(insns).forEach(list::add);
        insertBefores.put(next, list);
        return this;
    }

    public InsnListModifier replace(AbstractInsnNode old, InsnList replacement) {
        replacements.put(old, replacement);
        return this;
    }

    public InsnListModifier replace(AbstractInsnNode old, @NotNull AbstractInsnNode... insns) {
        var list = new InsnList();
        List.of(insns).forEach(list::add);
        replacements.put(old, list);
        return this;
    }

    public InsnListModifier remove(AbstractInsnNode insn) {
        removals.add(insn);
        return this;
    }

    public InsnListModifier remove(@NotNull AbstractInsnNode... insns) {
        List.of(insns).forEach(this::remove);
        return this;
    }

    public InsnListModifier remove(Iterable<? extends AbstractInsnNode> iterable) {
        iterable.forEach(this::remove);
        return this;
    }

    public void apply(InsnList insns) {
        appends.forEach(insns::add);
        prepends.forEach(insns::insert);
        inserts.forEach(insns::insert);
        insertBefores.forEach(insns::insertBefore);
        replacements.forEach((old, replacement) -> {
            insns.insert(old, replacement);
            insns.remove(old);
        });
        removals.forEach(insns::remove);
    }

    public void apply(MethodNode node) {
        apply(node.instructions);
    }
}

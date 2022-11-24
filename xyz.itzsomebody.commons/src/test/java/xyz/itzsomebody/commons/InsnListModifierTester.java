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

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

import java.util.stream.IntStream;

public class InsnListModifierTester {
    @Test
    public void testAppending() {
        var insns = new InsnList();
        var addMe = new InsnList();
        addMe.add(new InsnNode(Opcodes.ACONST_NULL));
        addMe.add(new InsnNode(Opcodes.ARETURN));
        var modifier = new InsnListModifier();
        modifier.append(addMe);
        modifier.apply(insns);
        IntStream.range(0, addMe.size()).forEach(i -> Assert.assertEquals(addMe.get(i), insns.get(i)));
    }

    @Test
    public void testPrepending() {
        var insns = new InsnList();
        insns.add(new InsnNode(Opcodes.ICONST_1));
        insns.add(new InsnNode(Opcodes.POP));
        var addMe = new InsnList();
        addMe.add(new InsnNode(Opcodes.ACONST_NULL));
        addMe.add(new InsnNode(Opcodes.ARETURN));
        var modifier = new InsnListModifier();
        modifier.prepend(addMe);
        modifier.apply(insns);
        IntStream.range(0, addMe.size()).forEach(i -> Assert.assertEquals(addMe.get(i), insns.get(i)));
    }

    @Test
    public void testInsertion() {
        var insns = new InsnList();
        insns.add(new InsnNode(Opcodes.ICONST_1));
        insns.add(new InsnNode(Opcodes.POP));
        var addMe = new InsnList();
        addMe.add(new InsnNode(Opcodes.ACONST_NULL));
        addMe.add(new InsnNode(Opcodes.ARETURN));
        var modifier = new InsnListModifier();
        modifier.insert(insns.getLast(), addMe);
        modifier.apply(insns);
        insns.remove(insns.getFirst());
        insns.remove(insns.getFirst());
        IntStream.range(0, addMe.size()).forEach(i -> Assert.assertEquals(addMe.get(i), insns.get(i)));
    }

    @Test
    public void testInsertionBefore() {
        var insns = new InsnList();
        insns.add(new InsnNode(Opcodes.ACONST_NULL));
        insns.add(new InsnNode(Opcodes.ARETURN));
        var addMe = new InsnList();
        addMe.add(new InsnNode(Opcodes.ICONST_1));
        addMe.add(new InsnNode(Opcodes.POP));

        var modifier = new InsnListModifier();
        modifier.insertBefore(insns.getFirst(), addMe);
        modifier.apply(insns);
        insns.remove(insns.getLast());
        insns.remove(insns.getLast());
        IntStream.range(0, addMe.size()).forEach(i -> Assert.assertEquals(addMe.get(i), insns.get(i)));
    }
}

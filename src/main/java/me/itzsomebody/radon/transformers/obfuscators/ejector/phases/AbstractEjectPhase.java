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

package me.itzsomebody.radon.transformers.obfuscators.ejector.phases;

import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.transformers.obfuscators.ejector.EjectorContext;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractEjectPhase implements Opcodes {
    protected final EjectorContext ejectorContext;

    public AbstractEjectPhase(EjectorContext ejectorContext) {
        this.ejectorContext = ejectorContext;
    }

    // TODO: Improve name generation logic
    protected static String getProxyMethodName(MethodNode methodNode) {
        String name = methodNode.name.replace('<', '_').replace('>', '_');
        return name + "$" + Math.abs(RandomUtils.getRandomInt());
    }

    protected static int getRandomAccess() {
        int access = ACC_STATIC;
        if (RandomUtils.getRandomBoolean())
            access += ACC_PRIVATE;
        if (RandomUtils.getRandomBoolean())
            access += ACC_SYNTHETIC;
        if (RandomUtils.getRandomBoolean())
            access += ACC_BRIDGE;
        return access;
    }

    protected static void insertFixes(MethodNode methodNode, Map<Integer, InsnList> fixes, int idVariable) {
        InsnList proxyFix = new InsnList();
        LabelNode end = new LabelNode();

        ArrayList<Integer> keys = new ArrayList<>(fixes.keySet());
        Collections.shuffle(keys);

        keys.forEach(id -> {
            int xorKey = RandomUtils.getRandomInt();

            InsnList insnList = fixes.get(id);
            proxyFix.add(new VarInsnNode(Opcodes.ILOAD, idVariable));
            proxyFix.add(new LdcInsnNode(xorKey));
            proxyFix.add(new InsnNode(IXOR));
            proxyFix.add(new LdcInsnNode(id ^ xorKey));
            LabelNode labelNode = new LabelNode();
            proxyFix.add(new JumpInsnNode(Opcodes.IF_ICMPNE, labelNode));

            proxyFix.add(insnList);
            proxyFix.add(new JumpInsnNode(Opcodes.GOTO, end));
            proxyFix.add(labelNode);
        });

        proxyFix.add(end);
        methodNode.instructions.insert(proxyFix);
    }

    public abstract void process(MethodWrapper methodWrapper, Frame<AbstractValue>[] frames);

    protected int getJunkArgumentCount() {
        if (ejectorContext.getJunkArgumentStrength() == 0)
            return 0;
        return RandomUtils.getRandomInt(ejectorContext.getJunkArgumentStrength() / 2, ejectorContext.getJunkArgumentStrength());
    }
}

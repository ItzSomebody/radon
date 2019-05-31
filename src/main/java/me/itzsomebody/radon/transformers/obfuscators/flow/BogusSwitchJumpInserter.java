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

package me.itzsomebody.radon.transformers.obfuscators.flow;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.StackHeightZeroFinder;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exceptions.StackEmulationException;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BogusSwitchJumpInserter extends FlowObfuscation {
    private static final int PRED_ACCESS = ACC_PUBLIC | ACC_STATIC | ACC_FINAL;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            AtomicBoolean shouldAdd = new AtomicBoolean();
            FieldNode predicate = new FieldNode(PRED_ACCESS, uniqueRandomString(), "I", null, null);

            classWrapper.getMethods().stream().filter(mw -> !excluded(mw) && mw.hasInstructions()).forEach(mw -> {
                InsnList insns = mw.getInstructions();

                int leeway = mw.getLeewaySize();
                int varIndex = mw.getMaxLocals();
                mw.getMethodNode().maxLocals++; // Prevents breaking of other transformers which rely on this field.

                StackHeightZeroFinder shzf = new StackHeightZeroFinder(mw.getMethodNode(), insns.getLast());
                try {
                    shzf.execute(false);
                } catch (StackEmulationException e) {
                    e.printStackTrace();
                    throw new RadonException(String.format("Error happened while trying to emulate the stack of %s.%s%s",
                            classWrapper.getName(), mw.getName(), mw.getDescription()));
                }

                Set<AbstractInsnNode> check = shzf.getEmptyAt();
                ArrayList<AbstractInsnNode> emptyAt = new ArrayList<>(check);

                if (emptyAt.size() <= 5 || leeway <= 30000)
                    return;

                int nTargets = emptyAt.size() / 2;

                ArrayList<LabelNode> targets = new ArrayList<>();
                for (int i = 0; i < nTargets; i++)
                    targets.add(new LabelNode());

                LabelNode back = new LabelNode();
                LabelNode dflt = new LabelNode();
                TableSwitchInsnNode tsin = new TableSwitchInsnNode(0, targets.size() - 1, dflt, targets.toArray(new LabelNode[0]));

                InsnList block = new InsnList();
                block.add(new VarInsnNode(ILOAD, varIndex));
                block.add(new JumpInsnNode(IFEQ, dflt));
                block.add(back);
                block.add(new VarInsnNode(ILOAD, varIndex));
                block.add(tsin);
                block.add(dflt);

                AbstractInsnNode switchTarget = emptyAt.get(RandomUtils.getRandomInt(emptyAt.size()));

                insns.insertBefore(switchTarget, block);

                targets.forEach(target -> {
                    AbstractInsnNode here = insns.getLast();

                    InsnList landing = new InsnList();
                    landing.add(target);
                    landing.add(ASMUtils.getNumberInsn(RandomUtils.getRandomInt(nTargets)));
                    landing.add(new VarInsnNode(ISTORE, varIndex));
                    landing.add(new JumpInsnNode(GOTO, targets.get(RandomUtils.getRandomInt(targets.size()))));

                    insns.insert(here, landing);
                });

                insns.insert(new VarInsnNode(ISTORE, varIndex));
                insns.insert(new FieldInsnNode(GETSTATIC, classWrapper.getName(), predicate.name, "I"));

                counter.addAndGet(targets.size());
                shouldAdd.set(true);
            });

            if (shouldAdd.get())
                classWrapper.addField(predicate);
        });

        Main.info("Inserted " + counter.get() + " bogus switch jumps");
    }
}

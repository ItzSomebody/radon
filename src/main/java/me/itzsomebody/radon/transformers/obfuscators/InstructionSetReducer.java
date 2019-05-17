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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.Map;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

/**
 * Removes tableswitch and *const_*
 */
public class InstructionSetReducer extends Transformer {
    @Override
    public void transform() {
        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(cw ->
                cw.getMethods().stream().filter(mw -> !excluded(mw)).forEach(mw -> {
                    InsnList newInsns = new InsnList();
                    insn:
                    for (AbstractInsnNode abstractInsnNode : mw.getInstructions().toArray()) {
                        if (abstractInsnNode instanceof TableSwitchInsnNode) {
                            LabelNode trampolineStart = new LabelNode();
                            InsnNode cleanStack = new InsnNode(Opcodes.POP);
                            JumpInsnNode jmpDefault = new JumpInsnNode(Opcodes.GOTO, ((TableSwitchInsnNode) abstractInsnNode).dflt);
                            LabelNode endOfTrampoline = new LabelNode();
                            JumpInsnNode skipTrampoline = new JumpInsnNode(Opcodes.GOTO, endOfTrampoline);

                            // Goto default trampoline
                            newInsns.add(skipTrampoline);
                            newInsns.add(trampolineStart);
                            newInsns.add(cleanStack);
                            newInsns.add(jmpDefault);
                            newInsns.add(endOfTrampoline);

                            // < min
                            // I(val)
                            newInsns.add(new InsnNode(Opcodes.DUP));
                            // I(val) I(val)
                            newInsns.add(new LdcInsnNode(-((TableSwitchInsnNode) abstractInsnNode).min));
                            // I(val) I(val) I(-min)
                            newInsns.add(new InsnNode(Opcodes.IADD));
                            // I(val) I(val-min)
                            newInsns.add(new JumpInsnNode(Opcodes.IFLT, trampolineStart));
                            // I(val)
                            // > max
                            newInsns.add(new InsnNode(Opcodes.DUP));
                            // I(val) I(val)
                            newInsns.add(new LdcInsnNode(-((TableSwitchInsnNode) abstractInsnNode).max));
                            // I(val) I(val) I(-max)
                            newInsns.add(new InsnNode(Opcodes.IADD));
                            // I(val) I(val-max)
                            newInsns.add(new JumpInsnNode(Opcodes.IFGT, trampolineStart));
                            // I(val)
                            // = VAL
                            newInsns.add(new InsnNode(Opcodes.DUP));
                            // I(val) I(val)
                            newInsns.add(new LdcInsnNode(-((TableSwitchInsnNode) abstractInsnNode).min));
                            // I(val) I(val) I(-min)
                            newInsns.add(new InsnNode(Opcodes.IADD));
                            // I(val) I(val-min) => 0 = first label, 1 = second label...

                            int labelIndex = 0;
                            for (LabelNode label : ((TableSwitchInsnNode) abstractInsnNode).labels) {
                                LabelNode nextBranch = new LabelNode();
                                newInsns.add(new InsnNode(Opcodes.DUP));
                                newInsns.add(new JumpInsnNode(Opcodes.IFNE, nextBranch));
                                newInsns.add(new InsnNode(Opcodes.POP));
                                newInsns.add(new InsnNode(Opcodes.POP));
                                newInsns.add(new JumpInsnNode(Opcodes.GOTO, label));

                                newInsns.add(nextBranch);
                                if (labelIndex + 1 != ((TableSwitchInsnNode) abstractInsnNode).labels.size()) {
                                    newInsns.add(new LdcInsnNode(-1));
                                    newInsns.add(new InsnNode(Opcodes.IADD));
                                }

                                labelIndex++;
                            }
                            // I(val) I(val-min-totalN)
                            newInsns.add(new InsnNode(Opcodes.POP));
                            // newInsns.add(new InsnNode(Opcodes.POP));
                            newInsns.add(new JumpInsnNode(Opcodes.GOTO, trampolineStart));
                            // I(val)
                        } else {
                            switch (abstractInsnNode.getOpcode()) {
                                case Opcodes.ICONST_M1:
                                case Opcodes.ICONST_0:
                                case Opcodes.ICONST_1:
                                case Opcodes.ICONST_2:
                                case Opcodes.ICONST_3:
                                case Opcodes.ICONST_4:
                                case Opcodes.ICONST_5:
                                    newInsns.add(new LdcInsnNode(abstractInsnNode.getOpcode() - 3));
                                    continue insn;
                                case Opcodes.LCONST_0:
                                case Opcodes.LCONST_1:
                                    newInsns.add(new LdcInsnNode(abstractInsnNode.getOpcode() - 9L));
                                    continue insn;
                                case Opcodes.FCONST_0:
                                case Opcodes.FCONST_1:
                                case Opcodes.FCONST_2:
                                    newInsns.add(new LdcInsnNode(abstractInsnNode.getOpcode() - 11F));
                                    continue insn;
                                case Opcodes.DCONST_0:
                                case Opcodes.DCONST_1:
                                    newInsns.add(new LdcInsnNode(abstractInsnNode.getOpcode() - 14D));
                                    continue insn;
                            }
                        }

                        newInsns.add(abstractInsnNode);
                    }

                    mw.setInstructions(newInsns);
                }));
    }

    @Override
    public String getName() {
        return "Instruction Set Reducer";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.INSTRUCTION_SET_REDUCER;
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // Not needed
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.INSTRUCTION_SET_REDUCER + " expects a boolean");
    }
}

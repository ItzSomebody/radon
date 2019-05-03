package me.itzsomebody.radon.transformers.obfuscators;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Removes tableswitch and *const_*
 */
public class InstructionSetReducer extends Transformer {
	@Override
	public void transform() {
		getClassWrappers().forEach(clazz -> {
			clazz.methods.forEach(method -> {
				InsnList newInsns = new InsnList();
				insn: for (AbstractInsnNode abstractInsnNode : method.methodNode.instructions.toArray()) {
					if (abstractInsnNode instanceof TableSwitchInsnNode) {
						LabelNode trampolineStart = new LabelNode();
						InsnNode cleanStack = new InsnNode(Opcodes.POP);
						JumpInsnNode jmpDefault = new JumpInsnNode(Opcodes.GOTO,
								((TableSwitchInsnNode) abstractInsnNode).dflt);
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
						continue insn;
					} else {
						switch (abstractInsnNode.getOpcode()) {
						case Opcodes.ICONST_M1:
							newInsns.add(new LdcInsnNode(-1));
							continue insn;
						case Opcodes.ICONST_0:
							newInsns.add(new LdcInsnNode(0));
							continue insn;
						case Opcodes.ICONST_1:
							newInsns.add(new LdcInsnNode(1));
							continue insn;
						case Opcodes.ICONST_2:
							newInsns.add(new LdcInsnNode(2));
							continue insn;
						case Opcodes.ICONST_3:
							newInsns.add(new LdcInsnNode(3));
							continue insn;
						case Opcodes.ICONST_4:
							newInsns.add(new LdcInsnNode(4));
							continue insn;
						case Opcodes.ICONST_5:
							newInsns.add(new LdcInsnNode(5));
							continue insn;
						case Opcodes.LCONST_0:
							newInsns.add(new LdcInsnNode(0L));
							continue insn;
						case Opcodes.LCONST_1:
							newInsns.add(new LdcInsnNode(1L));
							continue insn;
						case Opcodes.FCONST_0:
							newInsns.add(new LdcInsnNode(0F));
							continue insn;
						case Opcodes.FCONST_1:
							newInsns.add(new LdcInsnNode(1F));
							continue insn;
						case Opcodes.FCONST_2:
							newInsns.add(new LdcInsnNode(2F));
							continue insn;
						case Opcodes.DCONST_0:
							newInsns.add(new LdcInsnNode(0D));
							continue insn;
						case Opcodes.DCONST_1:
							newInsns.add(new LdcInsnNode(1D));
							continue insn;
						}
					}
					newInsns.add(abstractInsnNode);
				}
				method.methodNode.instructions = newInsns;
			});
		});
	}

	@Override
	public String getName() {
		return "Instruction set reducer";
	}

	@Override
	public ExclusionType getExclusionType() {
		return ExclusionType.CRASHER;
	}

	@Override
	public Object getConfiguration() {
		return null;
	}

	@Override
	public void setConfiguration(Map<String, Object> config) {

	}

	@Override
	public void verifyConfiguration(Map<String, Object> config) {

	}
}

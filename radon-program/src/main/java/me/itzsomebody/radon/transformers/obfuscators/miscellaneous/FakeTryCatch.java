package me.itzsomebody.radon.transformers.obfuscators.miscellaneous;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.AccessUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

//It may cause IllegalArgument with Other Transformers,but i don't know what's wrong.
public class FakeTryCatch extends Transformer {
	@Override
	public void transform() {
		AtomicInteger counter = new AtomicInteger();

		getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
			ClassNode classNode = classWrapper.classNode;

			if (classNode.methods != null)
				classNode.methods
						.parallelStream().filter(imethod -> !imethod.name.startsWith("<")
								&& !AccessUtils.isNative(imethod.access) && !AccessUtils.isAbstract(imethod.access))
						.forEach(mn -> {
							// Very Nice
							if (mn.localVariables != null) {
								for (int i = 1 + ((mn.localVariables.size() + 1) / 2); i > 0; i--) {
									LabelNode begin = new LabelNode();
									LabelNode handler = new LabelNode();
									LabelNode end = new LabelNode();
									if (mn.localVariables != null) {
										mn.instructions.insert(begin);
										mn.instructions.add(end);
										mn.instructions.add(handler);

										// mn.instructions.add(new /
										// LdcInsnNode(StringUtils.randomAlphaNumericString(11)));

										// mn.instructions.add(new InsnNode(Opcodes.POP2));
										// mn.instructions.add(new InsnNode(Opcodes.POP));

										mn.instructions.add(new LdcInsnNode(StringUtils.randomAlphaNumericString(16)));
										mn.instructions.add(new InsnNode(Opcodes.ATHROW));

										LocalVariableNode ex = new LocalVariableNode(
												StringUtils.randomAlphaNumericString(70),
												"L" + "java/lang/Throwable" + ";", null, begin, handler,
												mn.localVariables.size());
										TryCatchBlockNode tryBlock = new TryCatchBlockNode(begin, end, handler,
												"L" + StringUtils.randomAlphaNumericString(70) + ";");
										mn.localVariables.add(ex);
										mn.tryCatchBlocks.add(tryBlock);
										mn.exceptions.add("java/lang/Throwable");
									}
								}
								counter.incrementAndGet();
							}
						});
		});

		LoggerUtils.stdOut(String.format("%d FakeTryCatch Added.", counter.get()));
	}

	@Override
	protected ExclusionType getExclusionType() {
		return ExclusionType.FAKETRYCATCH;
	}

	@Override
	public String getName() {
		return "FakeTryCatch";
	}
}

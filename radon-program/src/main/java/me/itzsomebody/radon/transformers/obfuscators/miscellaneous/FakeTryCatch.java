package me.itzsomebody.radon.transformers.obfuscators.miscellaneous;

import java.util.concurrent.atomic.AtomicInteger;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.AccessUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

//It may cause IllegalArgument with Other Transformers,but i don't know what's wrong.
public class FakeTryCatch extends Transformer {
	@Override
	public void transform() {
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger counter_Shiped = new AtomicInteger();

		// Warnning!
		// set it true
		// to set Handler to class itself
		boolean Crasher = false;
		// it will got Verify Excep
		// do -noverify to bypass

		// Nice Naming
		String FAKE_Handler = "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////;//)//goto//break//META-INF//public//static//throw//throw//new//"
				+ getANSI() + "//" + ")};" + getANSI() + StringUtils.randomAlphaNumericString(12) + '\u0000';
		ClassNode FAKE_Node = this.genHandler(FAKE_Handler);

		getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
			ClassNode classNode = classWrapper.classNode;

			if (classNode.methods != null)
				classNode.methods
						.parallelStream().filter(imethod -> !imethod.name.startsWith("<")
								&& !AccessUtils.isNative(imethod.access) && !AccessUtils.isAbstract(imethod.access))
						.forEach(mn -> {
							// Very Nice
							if (mn.localVariables != null) {
								doobfu: {
									for (int i = 1 + ((mn.localVariables.size() + 1) / 2); i > 0; i--) {
										LabelNode begin = new LabelNode();
										LabelNode handler = new LabelNode();
										LabelNode end = new LabelNode();

										// Ship the Method if it has athrow.
										for (AbstractInsnNode ai = mn.instructions.getFirst(); ai != null; ai = ai
												.getNext()) {
											counter_Shiped.incrementAndGet();
											if (Opcodes.ATHROW == ai.getOpcode())
												break doobfu;

											counter_Shiped.decrementAndGet();
										}
										if (mn.localVariables != null) {
											mn.instructions.insert(begin);
											mn.instructions.add(end);
											mn.instructions.insert(end, new LdcInsnNode(getANSI()));

											// Bypass Some deobfuscator
											mn.instructions.add(handler);
											mn.instructions.insert(handler, new LdcInsnNode(getANSI()));

											mn.instructions.add(new LdcInsnNode(getANSI()));

											mn.instructions.add(new LdcInsnNode(
													getANSI() + StringUtils.randomAlphaNumericString(16)));
											mn.instructions.add(new InsnNode(Opcodes.ATHROW));

											LocalVariableNode ex = new LocalVariableNode(
													(Crasher ? classNode.name : FAKE_Handler), "L" + FAKE_Handler + ";",
													null, begin, handler, mn.localVariables.size());
											TryCatchBlockNode tryBlock = new TryCatchBlockNode(begin, end, handler,
													"L" + (Crasher ? classNode.name : FAKE_Handler) + ";");
											mn.localVariables.add(ex);
											mn.tryCatchBlocks.add(tryBlock);
											mn.exceptions.add(FAKE_Handler);
										}
									}

									counter.incrementAndGet();
								}
							}
						});
		});

		LoggerUtils.stdOut(String.format("%d FakeTryCatch Added.", counter.get()));
		LoggerUtils.stdOut(String.format("%d Method Shiped.", counter_Shiped.get()));
	}

	@Override
	protected ExclusionType getExclusionType() {
		return ExclusionType.FAKETRYCATCH;
	}

	@Override
	public String getName() {
		return "FakeTryCatch";
	}

	public ClassNode genHandler(String na) {
		ClassNode classNode = createClass(na);
		ClassWriter cw = new ClassWriter(0);
		cw.newUTF8("Radon_Fake_TryCatch_Extend");
		classNode.accept(cw);

		this.getResources().put(classNode.name + ".class", cw.toByteArray());
		return classNode;
	}

	private ClassNode createClass(String className) {
		ClassNode classNode = new ClassNode();
		classNode.visit(49, ACC_SUPER + ACC_PUBLIC, className, null, "java/lang/Throwable", null);

		MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		classNode.visitEnd();

		return classNode;
	}

	public static String getANSI() {
		// Magic.
		// just some ANSI Control chars.
		return decodeUnicode("\u202b\u202e\u202e\u200c\u202c\u206b\u001e\u001f");
	}

	public static String decodeUnicode(String unicode) {
		StringBuffer sb = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {
			int data = Integer.parseInt(hex[i], 16);
			sb.append((char) data);
		}
		return sb.toString();
	}
}

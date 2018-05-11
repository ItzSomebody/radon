package me.itzsomebody.radon.transformers.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Transformer that takes all the strings in a class and pools them into a
 * method. When the string is needed, the
 * string pool method is called with an index number.
 *
 * @author ItzSomebody
 */
public class StringPool extends AbstractTransformer {
    /**
     * Path to pool method.
     */
    private String randName;

    /**
     * Array of {@link String}s that will be pooled
     */
    private String[] strings;

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started string pool transformer."));
        this.randName = StringUtils.randomString(this.dictionary);
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "StringPool")).forEach(classNode -> {
            List<String> stringslist = new ArrayList<>();
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "StringPool")
                            && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (insn instanceof LdcInsnNode) {
                        Object cst = ((LdcInsnNode) insn).cst;

                        if (cst instanceof String) {
                            stringslist.add((String) cst);

                            int indexNumber = stringslist.size() - 1;

                            methodNode.instructions.insertBefore(insn,
                                    BytecodeUtils.getNumberInsn(indexNumber));
                            methodNode.instructions.set(insn,
                                    new MethodInsnNode(INVOKESTATIC, classNode.name, randName, "(I)Ljava/lang/String;", false));
                            counter.incrementAndGet();
                        }
                    }
                }
            });
            if (stringslist.size() != 0) {
                this.strings = new String[stringslist.size()];
                for (int i = 0; i < stringslist.size(); i++) {
                    this.strings[i] = stringslist.get(i);
                }
                classNode.methods.add(stringPool());
            }
        });
        this.logStrings.add(LoggerUtils.stdOut("Pooled  " + counter + " strings."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * String pool method which contains all the strings.
     *
     * @return string pool method which contains all the strings.
     */
    private MethodNode stringPool() {
        MethodNode method = new MethodNode(ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC + ACC_BRIDGE, randName, "(I)Ljava/lang/String;", null, null);

        method.visitCode();

        Label l0 = new Label();
        method.visitLabel(l0);
        int numberOfStrings = this.strings.length;
        if (numberOfStrings <= 5) {
            method.visitInsn(numberOfStrings + 3);
        } else if (numberOfStrings <= 127) {
            method.visitIntInsn(BIPUSH, this.strings.length);
        } else if (numberOfStrings <= 32767) {
            method.visitIntInsn(SIPUSH, this.strings.length);
        } else {
            method.visitLdcInsn(this.strings.length);
        }

        method.visitTypeInsn(ANEWARRAY, "java/lang/String");

        for (int i = 0; i < this.strings.length; i++) {
            method.visitInsn(DUP);

            if (i <= 5) {
                method.visitInsn(i + 3);
            } else if (i <= 127) {
                method.visitIntInsn(BIPUSH, i);
            } else if (i <= 32767) {
                method.visitIntInsn(SIPUSH, i);
            } else {
                method.visitLdcInsn(i);
            }

            method.visitLdcInsn(this.strings[i]);
            method.visitInsn(AASTORE);
        }
        method.visitVarInsn(ASTORE, 1);
        Label l1 = new Label();
        method.visitLabel(l1);
        method.visitVarInsn(ALOAD, 1);
        method.visitVarInsn(ILOAD, 0);
        method.visitInsn(AALOAD);
        method.visitVarInsn(ASTORE, 2);
        Label l2 = new Label();
        method.visitLabel(l2);
        method.visitVarInsn(ALOAD, 2);
        method.visitInsn(ARETURN);

        method.visitMaxs(4, 3);

        method.visitEnd();

        return method;
    }
}

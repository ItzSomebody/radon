package me.itzsomebody.radon.transformers.misc;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that takes all the strings in a class and pools them into a method. When the string is needed, the
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
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting string pool transformer."));
        long current = System.currentTimeMillis();
        AtomicInteger counter = new AtomicInteger();
        randName = StringUtils.crazyString();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            List<String> stringslist = new ArrayList<>();
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (insn instanceof LdcInsnNode) {
                        Object cst = ((LdcInsnNode) insn).cst;

                        if (cst instanceof String) {
                            stringslist.add((String) cst);

                            int indexNumber = stringslist.size() - 1;

                            methodNode.instructions.insertBefore(insn, BytecodeUtils.getNumberInsn(indexNumber));
                            methodNode.instructions.set(insn, new MethodInsnNode(INVOKESTATIC, classNode.name, randName, "(I)Ljava/lang/String;", false));
                            counter.incrementAndGet();
                        }
                    }
                }
            });
            if (stringslist.size() != 0) {
                strings = new String[stringslist.size()];
                for (int i = 0; i < stringslist.size(); i++) {
                    strings[i] = stringslist.get(i);
                }
                classNode.methods.add(stringPool());
            }
        });
        logStrings.add(LoggerUtils.stdOut("Pooled  " + counter + " strings."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
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
        int numberOfStrings = strings.length;
        if (numberOfStrings <= 5) {
            method.visitInsn(numberOfStrings + 3);
        } else if (numberOfStrings <= 127) {
            method.visitIntInsn(BIPUSH, strings.length);
        } else if (numberOfStrings <= 32767) {
            method.visitIntInsn(SIPUSH, strings.length);
        } else {
            method.visitLdcInsn(strings.length);
        }

        method.visitTypeInsn(ANEWARRAY, "java/lang/String");

        for (int i = 0; i < strings.length; i++) {
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

            method.visitLdcInsn(strings[i]);
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

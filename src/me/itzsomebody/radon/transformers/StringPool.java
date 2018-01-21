package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Label;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that takes all the strings in a class and pools them into a method. When the string is needed, the
 * string pool method is called with an index number.
 *
 * @author ItzSomebody
 */
public class StringPool {
    /**
     * The name the pool method will have.
     */
    private String randName;

    /**
     * Array of {@link String}s that will be pooled
     */
    private String[] strings;

    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link StringPool} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public StringPool(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link StringPool#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting source name removal transformer"));
        randName = StringUtils.crazyString();
        ArrayList<String> stringslist = new ArrayList<>();

        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "/" + methodNode.name)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn instanceof LdcInsnNode) {
                    Object cst = ((LdcInsnNode) insn).cst;

                    if (cst instanceof String) {
                        stringslist.add((String) cst);

                        int indexNumber = stringslist.size() - 1;
                        if (indexNumber >= 0 && indexNumber <= 5) {
                            methodNode.instructions.insertBefore(insn, BytecodeUtils.getIConst(indexNumber));
                        } else if (indexNumber >= 6 && indexNumber <= 32767) {
                            methodNode.instructions.insertBefore(insn, BytecodeUtils.getIntInsn(indexNumber));
                        } else {
                            methodNode.instructions.insertBefore(insn, new LdcInsnNode(indexNumber));
                        }

                        methodNode.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, randName, "(I)Ljava/lang/String;", false));
                    }
                }
            }
        }
        if (stringslist.size() != 0) {
            strings = new String[stringslist.size()];
            for (int i = 0; i < stringslist.size(); i++) {
                strings[i] = stringslist.get(i);
            }
            classNode.methods.add(stringPool());
        }
        logStrings.add(LoggerUtils.stdOut("Finished pooling strings"));
        logStrings.add(LoggerUtils.stdOut("Pooled " + stringslist.size() + " strings"));
    }

    /**
     * String pool method which contains all the strings.
     *
     * @return string pool method which contains all the strings.
     */
    private MethodNode stringPool() {
        MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, randName, "(I)Ljava/lang/String;", null, null);

        method.visitCode();

        Label l0 = new Label();
        method.visitLabel(l0);
        int numberOfStrings = strings.length;
        if (numberOfStrings <= 5) {
            method.visitInsn(numberOfStrings + 3);
        } else if (numberOfStrings <= 127) {
            method.visitIntInsn(Opcodes.BIPUSH, strings.length);
        } else if (numberOfStrings <= 32767) {
            method.visitIntInsn(Opcodes.SIPUSH, strings.length);
        } else {
            method.visitLdcInsn(strings.length);
        }

        method.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");

        for (int i = 0; i < strings.length; i++) {
            method.visitInsn(Opcodes.DUP);

            if (i <= 5) {
                method.visitInsn(i + 3);
            } else if (i <= 127) {
                method.visitIntInsn(Opcodes.BIPUSH, i);
            } else if (i <= 32767) {
                method.visitIntInsn(Opcodes.SIPUSH, i);
            } else {
                method.visitLdcInsn(i);
            }

            method.visitLdcInsn(strings[i]);
            method.visitInsn(Opcodes.AASTORE);
        }
        method.visitVarInsn(Opcodes.ASTORE, 1);
        Label l1 = new Label();
        method.visitLabel(l1);
        method.visitVarInsn(Opcodes.ALOAD, 1);
        method.visitVarInsn(Opcodes.ILOAD, 0);
        method.visitInsn(Opcodes.AALOAD);
        method.visitVarInsn(Opcodes.ASTORE, 2);
        Label l2 = new Label();
        method.visitLabel(l2);
        method.visitVarInsn(Opcodes.ALOAD, 2);
        method.visitInsn(Opcodes.ARETURN);

        method.visitMaxs(4, 3);

        method.visitEnd();

        return method;
    }

    /**
     * Returns {@link String}s to add to log.
     *
     * @return {@link String}s to add to log.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }
}

package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Label;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
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
            if (!((methodNode.access & Opcodes.ACC_ABSTRACT) == 0)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn instanceof LdcInsnNode) {
                    Object cst = ((LdcInsnNode) insn).cst;

                    if (cst instanceof String) {
                        stringslist.add((String) cst);
                        if (stringslist.size() - 1 == 0) {
                            methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.ICONST_0));
                        } else if (stringslist.size() - 1 == 1) {
                            methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.ICONST_1));
                        } else if (stringslist.size() - 1 == 2) {
                            methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.ICONST_2));
                        } else if (stringslist.size() - 1 == 3) {
                            methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.ICONST_3));
                        } else if (stringslist.size() - 1 == 4) {
                            methodNode.instructions.insertBefore(insn, new InsnNode(Opcodes.ICONST_4));
                        } else if (stringslist.size() - 1 > 4 && stringslist.size() - 1 < 127) {
                            methodNode.instructions.insertBefore(insn, new IntInsnNode(Opcodes.BIPUSH, stringslist.size() - 1));
                        } else if (stringslist.size() - 1 >= 127 && stringslist.size() - 1 < 32767) {
                            methodNode.instructions.insertBefore(insn, new IntInsnNode(Opcodes.SIPUSH, stringslist.size() - 1));
                        } else {
                            methodNode.instructions.insertBefore(insn, new LdcInsnNode(stringslist.size() - 1));
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
        if (strings.length > 5 && strings.length < 127) {
            method.visitIntInsn(Opcodes.BIPUSH, strings.length);
        } else if (strings.length >= 127 && strings.length < 32767) {
            method.visitIntInsn(Opcodes.SIPUSH, strings.length);
        } else if (strings.length == 1) {
            method.visitInsn(Opcodes.ICONST_1);
        } else if (strings.length == 2) {
            method.visitInsn(Opcodes.ICONST_2);
        } else if (strings.length == 3) {
            method.visitInsn(Opcodes.ICONST_3);
        } else if (strings.length == 4) {
            method.visitInsn(Opcodes.ICONST_4);
        } else if (strings.length == 5) {
            method.visitInsn(Opcodes.ICONST_5);
        } else {
            method.visitLdcInsn(strings.length);
        }

        method.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");

        for (int i = 0; i < strings.length; i++) {

            method.visitInsn(Opcodes.DUP);

            if (i == 0) {
                method.visitInsn(Opcodes.ICONST_0);
            } else if (i == 1) {
                method.visitInsn(Opcodes.ICONST_1);
            } else if (i == 2) {
                method.visitInsn(Opcodes.ICONST_2);
            } else if (i == 3) {
                method.visitInsn(Opcodes.ICONST_3);
            } else if (i == 4) {
                method.visitInsn(Opcodes.ICONST_4);
            } else if (i > 4 && i < 127) {
                method.visitIntInsn(Opcodes.BIPUSH, i);
            } else if (i >= 127 && i < 32767) {
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

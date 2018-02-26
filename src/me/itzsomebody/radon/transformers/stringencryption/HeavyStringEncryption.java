package me.itzsomebody.radon.transformers.stringencryption;

import me.itzsomebody.radon.methods.StringEncryption;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.concurrent.atomic.AtomicInteger;

public class HeavyStringEncryption extends AbstractTransformer {
    /**
     * Indication to not encrypt strings containing Spigot placeholders (%%__USER__%%, %%__RESOURCE__%% and %%__NONCE__%%).
     */
    private boolean spigotMode;

    /**
     * Constructor used to create a {@link HeavyStringEncryption} object.
     *
     * @param spigotMode indication to not encrypt strings containing Spigot placeholders (%%__USER__%%, %%__RESOURCE__%% and %%__NONCE__%%).
     */
    public HeavyStringEncryption(boolean spigotMode) {
        this.spigotMode = spigotMode;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting heavy string encryption transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String[] decryptorPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.crazyString()};
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (insn instanceof LdcInsnNode) {
                        Object cst = ((LdcInsnNode) insn).cst;

                        if (cst instanceof String) {
                            if (spigotMode &&
                                    ((String) cst).contains("%%__USER__%%")
                                    || ((String) cst).contains("%%__RESOURCE__%%")
                                    || ((String) cst).contains("%%__NONCE__%%")) continue;

                            int key3 = NumberUtils.getRandomInt(25000) + 25000;
                            ((LdcInsnNode) insn).cst = StringUtils.heavyEncrypt(decryptorPath[0].replace("/", "."), decryptorPath[1], key3, ((String) ((LdcInsnNode) insn).cst));
                            methodNode.instructions.insert(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, decryptorPath[0], decryptorPath[1], "(Ljava/lang/Object;Ljava/lang/Object;I)Ljava/lang/String;", false));
                            methodNode.instructions.insert(insn, BytecodeUtils.getNumberInsn(key3));
                            methodNode.instructions.insert(insn, new InsnNode(ACONST_NULL));
                            counter.incrementAndGet();
                        }
                    }
                }
            });
        });

        classNodes().stream().filter(classNode -> classNode.name.equals(decryptorPath[0])).forEach(classNode -> {
            classNode.methods.add(StringEncryption.heavyMethod(decryptorPath[1]));
            classNode.access = BytecodeUtils.accessFixer(classNode.access);
        });
        logStrings.add(LoggerUtils.stdOut("Encrypted " + counter + " strings."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}

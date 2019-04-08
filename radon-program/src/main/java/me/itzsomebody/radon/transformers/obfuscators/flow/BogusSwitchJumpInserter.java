package me.itzsomebody.radon.transformers.obfuscators.flow;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.asm.StackHeightZeroFinder;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exceptions.StackEmulationException;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public class BogusSwitchJumpInserter extends FlowObfuscation {
    private static final int PRED_ACCESS = ACC_PUBLIC | ACC_STATIC | ACC_FINAL;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            FieldNode predicate = new FieldNode(PRED_ACCESS, randomString(), "I", null, null);

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;

                int leeway = getSizeLeeway(methodNode);
                int varIndex = methodNode.maxLocals;
                methodNode.maxLocals += 2; // Prevents breaking of other transformers which rely on this field.

                StackHeightZeroFinder stackHeightZeroFinder = new StackHeightZeroFinder(methodNode, methodNode.instructions.getLast());
                try {
                    stackHeightZeroFinder.execute(false);
                } catch (StackEmulationException e) {
                    e.printStackTrace();
                    throw new RadonException(String.format("Error happened while trying to emulate the stack of %s.%s%s",
                            classWrapper.classNode.name, methodNode.name, methodNode.desc));
                }

                Set<AbstractInsnNode> check = stackHeightZeroFinder.getEmptyAt();

                Frame<BasicValue>[] frames;
                try {
                    frames = new Analyzer<>(new BasicInterpreter()).analyze(classWrapper.classNode.name, methodNode);
                } catch (AnalyzerException e) {
                    e.printStackTrace();
                    throw new RadonException(String.format("Error happened while trying to analyze %s.%s%s",
                            classWrapper.classNode.name, methodNode.name, methodNode.desc));
                }

                for (int i = 0; i < methodNode.instructions.size(); i++) {
                    AbstractInsnNode insn = methodNode.instructions.get(i);
                    if (check.contains(insn) && frames[i] == null)
                        check.remove(insn); // Dead code should not be jumped to
                }

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

                methodNode.instructions.insertBefore(switchTarget, block);

                targets.forEach(target -> {
                    AbstractInsnNode here = methodNode.instructions.getLast();
                    LabelNode ignoreMePlz = new LabelNode();

                    InsnList landing = new InsnList();
                    landing.add(target);
                    landing.add(BytecodeUtils.getNumberInsn(RandomUtils.getRandomInt(nTargets)));
                    landing.add(new VarInsnNode(ISTORE, varIndex));
                    landing.add(new JumpInsnNode(GOTO, targets.get(RandomUtils.getRandomInt(targets.size()))));

                    methodNode.instructions.insert(here, landing);
                });

                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        new VarInsnNode(ISTORE, varIndex));
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        new FieldInsnNode(GETSTATIC, classWrapper.classNode.name, predicate.name, "I"));
            });

            classWrapper.classNode.fields.add(predicate);
        });

        Logger.stdOut("Inserted " + counter.get() + " bogus switch jumps");
    }
}

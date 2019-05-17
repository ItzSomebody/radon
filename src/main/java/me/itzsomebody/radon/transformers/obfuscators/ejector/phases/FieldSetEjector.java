package me.itzsomebody.radon.transformers.obfuscators.ejector.phases;

import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.transformers.obfuscators.ejector.EjectorContext;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.*;

public final class FieldSetEjector extends AbstractEjectPhase {
    public FieldSetEjector(EjectorContext ejectorContext) {
        super(ejectorContext);
    }

    private static Map<FieldSetInfo, List<FieldInsnNode>> analyzeFieldSets(MethodNode methodNode, Frame<AbstractValue>[] frames) {
        Map<FieldSetInfo, List<FieldInsnNode>> result = new HashMap<>();
        InsnList insnList = methodNode.instructions;
        ListIterator<AbstractInsnNode> iterator = insnList.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode abstractInsnNode = iterator.next();

            if (!(abstractInsnNode instanceof FieldInsnNode))
                continue;
            FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
            if (fieldInsnNode.getOpcode() != PUTFIELD && fieldInsnNode.getOpcode() != PUTSTATIC)
                continue;

            Frame<AbstractValue> frame = frames[insnList.indexOf(fieldInsnNode)];
            AbstractValue value = frame.getStack(frame.getStackSize() - 1);
            if (!value.isConstant())
                continue;

            FieldSetInfo fieldSetInfo = new FieldSetInfo(fieldInsnNode.getOpcode(), fieldInsnNode.desc);

            if (!result.containsKey(fieldSetInfo)) {
                ArrayList<FieldInsnNode> list = new ArrayList<>();
                list.add(fieldInsnNode);
                result.put(fieldSetInfo, list);
            } else {
                result.get(fieldSetInfo).add(fieldInsnNode);
            }
        }
        return result;
    }

    private static MethodNode createProxyMethod(String name, FieldSetInfo fieldSetInfo) {
        List<Type> arguments = new ArrayList<>();
        if (fieldSetInfo.opcode != PUTSTATIC)
            arguments.add(Type.getType(Object.class));
        arguments.add(Type.getType(fieldSetInfo.desc));
        arguments.add(Type.INT_TYPE);

        MethodNode methodNode = new MethodNode(getRandomAccess(), name, Type.getMethodDescriptor(Type.VOID_TYPE, arguments.toArray(new Type[0])),
                null, null);
        methodNode.instructions = ASMUtils.singletonList(new InsnNode(Opcodes.RETURN));
        return methodNode;
    }

    private Map<Integer, InsnList> createJunkArguments(List<FieldInsnNode> fieldInsnNodes, boolean isStatic) {
        Map<Integer, InsnList> junkArguments = new HashMap<>();

        for (int k = 0; k < getJunkArgumentCount(); k++) {
            FieldInsnNode fieldInsnNode = RandomUtils.getRandomElement(fieldInsnNodes);
            Type type = Type.getType(fieldInsnNode.desc);

            InsnList junkProxyArgumentFix = new InsnList();
            if (!isStatic) {
                junkProxyArgumentFix.add(new VarInsnNode(ALOAD, 0));
                junkProxyArgumentFix.add(new TypeInsnNode(CHECKCAST, fieldInsnNode.owner));
            }
            junkProxyArgumentFix.add(ASMUtils.getRandomValue(type));
            junkProxyArgumentFix.add(fieldInsnNode.clone(null));

            junkArguments.put(ejectorContext.getNextId(), junkProxyArgumentFix);
        }
        return junkArguments;
    }

    private InsnList processFieldSet(MethodNode methodNode, Frame<AbstractValue>[] frames, Map<AbstractInsnNode, InsnList> patches, FieldInsnNode fieldInsnNode) {
        InsnList proxyArgumentFix = new InsnList();
        Frame<AbstractValue> frame = frames[methodNode.instructions.indexOf(fieldInsnNode)];

        Type type = Type.getType(fieldInsnNode.desc);
        AbstractValue argumentValue = frame.getStack(frame.getStackSize() - 1);
        if (argumentValue.isConstant() && argumentValue.getUsages().size() == 1) {
            AbstractInsnNode valueInsn = ejectorContext.isJunkArguments() ? ASMUtils.getRandomValue(type) : ASMUtils.getDefaultValue(type);
            patches.put(argumentValue.getInsnNode(), ASMUtils.singletonList(valueInsn));
            if (fieldInsnNode.getOpcode() != PUTSTATIC) {
                proxyArgumentFix.add(new VarInsnNode(ALOAD, 0));
                proxyArgumentFix.add(new TypeInsnNode(CHECKCAST, fieldInsnNode.owner));
            }
            proxyArgumentFix.add(argumentValue.getInsnNode().clone(null));
            proxyArgumentFix.add(fieldInsnNode.clone(null));
        }

        return proxyArgumentFix;
    }

    @Override
    public void process(MethodWrapper methodWrapper, Frame<AbstractValue>[] frames) {
        ClassWrapper classWrapper = ejectorContext.getClassWrapper();
        MethodNode methodNode = methodWrapper.getMethodNode();

        Map<FieldSetInfo, List<FieldInsnNode>> fieldSets = analyzeFieldSets(methodNode, frames);
        if (fieldSets.isEmpty())
            return;
        methodWrapper.getMethodNode().maxStack++;

        Map<AbstractInsnNode, InsnList> patches = new HashMap<>();
        fieldSets.forEach((key, value) -> {
            MethodNode proxyMethod = createProxyMethod(getProxyMethodName(methodNode), key);
            boolean isStatic = key.opcode == PUTSTATIC;
            int offset = isStatic ? 0 : 1;

            Map<Integer, InsnList> proxyFixes = new HashMap<>();

            classWrapper.addMethod(proxyMethod);

            for (FieldInsnNode fieldInsnNode : value) {
                int id = ejectorContext.getNextId();

                patches.put(fieldInsnNode, ASMUtils.asList(
                        new LdcInsnNode(id),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, classWrapper.getName(), proxyMethod.name, proxyMethod.desc, false)
                ));


                InsnList proxyArgumentFix = processFieldSet(methodNode, frames, patches, fieldInsnNode);

                proxyFixes.put(id, proxyArgumentFix);
                ejectorContext.getCounter().incrementAndGet();

                if (ejectorContext.isJunkArguments()) {
                    proxyFixes.putAll(createJunkArguments(value, isStatic));
                }
            }

            int idVariable = Type.getArgumentTypes(proxyMethod.desc)[offset + 1].getSize();
            insertFixes(proxyMethod, proxyFixes, idVariable);
        });

        patches.forEach((abstractInsnNode, insnList) -> {
            methodNode.instructions.insertBefore(abstractInsnNode, insnList);
            methodNode.instructions.remove(abstractInsnNode);
        });
    }

    private static class FieldSetInfo {
        private final int opcode;
        private final String desc;

        FieldSetInfo(int opcode, String desc) {
            this.opcode = opcode;
            this.desc = desc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldSetInfo that = (FieldSetInfo) o;
            return opcode == that.opcode &&
                    Objects.equals(desc, that.desc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(opcode, desc);
        }
    }
}

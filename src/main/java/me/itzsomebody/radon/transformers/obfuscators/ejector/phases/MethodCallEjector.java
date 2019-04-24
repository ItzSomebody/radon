package me.itzsomebody.radon.transformers.obfuscators.ejector.phases;

import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.transformers.obfuscators.ejector.EjectorContext;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.*;

public class MethodCallEjector implements IEjectPhase, Opcodes {
    private final boolean junkArguments;

    public MethodCallEjector(boolean junkArguments) {
        this.junkArguments = junkArguments;
    }

    private static Map<MethodCallInfo, List<MethodInsnNode>> analyzeMethodCalls(MethodNode methodNode, Frame<AbstractValue>[] frames) {
        Map<MethodCallInfo, List<MethodInsnNode>> result = new HashMap<>();
        InsnList insnList = methodNode.instructions;
        ListIterator<AbstractInsnNode> iterator = insnList.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode abstractInsnNode = iterator.next();

            if (!(abstractInsnNode instanceof MethodInsnNode))
                continue;
            MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
            if ("<init>".equals(methodInsnNode.name))
                continue;

            Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);
            if (argumentTypes.length == 0)
                continue;

            int constantArguments = 0;
            Frame<AbstractValue> frame = frames[insnList.indexOf(methodInsnNode)];
            for (int i = 0; i < argumentTypes.length; i++) {
                AbstractValue value = frame.getStack(frame.getStackSize() - argumentTypes.length + i);
                if (value.isConstant() && value.getUsages().size() == 1)
                    constantArguments++;
            }

            if (constantArguments == 0)
                continue;

            MethodCallInfo methodCallInfo = new MethodCallInfo(methodInsnNode.owner, methodInsnNode.itf, methodInsnNode.getOpcode(), methodInsnNode.name, methodInsnNode.desc);

            if (!result.containsKey(methodCallInfo)) {
                ArrayList<MethodInsnNode> list = new ArrayList<>();
                list.add(methodInsnNode);
                result.put(methodCallInfo, list);
            } else {
                result.get(methodCallInfo).add(methodInsnNode);
            }
        }
        return result;
    }

    private static MethodNode createProxyMethod(String name, MethodCallInfo methodCallInfo) {
        List<Type> arguments = new ArrayList<>();
        if (methodCallInfo.opcode != INVOKESTATIC)
            arguments.add(Type.getType(Object.class));
        arguments.addAll(Arrays.asList(Type.getArgumentTypes(methodCallInfo.desc)));
        arguments.add(Type.INT_TYPE);

        Type returnType = Type.getReturnType(methodCallInfo.desc);

        int access = ACC_STATIC;
        if (RandomUtils.getRandomBoolean())
            access += ACC_PRIVATE;
        if (RandomUtils.getRandomBoolean())
            access += ACC_SYNTHETIC;
        if (RandomUtils.getRandomBoolean())
            access += ACC_BRIDGE;

        MethodNode methodNode = new MethodNode(access, name, Type.getMethodDescriptor(returnType, arguments.toArray(new Type[0])),
                null, null);
        InsnList insnList = new InsnList();

        int variable = 0;
        for (int i = 0; i < arguments.size() - 1; i++) {
            Type type = arguments.get(i);
            insnList.add(new VarInsnNode(ASMUtils.getVarOpcode(type, false), variable));
            if (i == 0 && methodCallInfo.opcode != INVOKESTATIC)
                insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, methodCallInfo.owner));
            variable += arguments.get(0).getSize();
        }

        insnList.add(new MethodInsnNode(methodCallInfo.opcode, methodCallInfo.owner, methodCallInfo.name, methodCallInfo.desc, methodCallInfo.isInterface));
        insnList.add(new InsnNode(ASMUtils.getReturnOpcode(returnType)));
        methodNode.instructions = insnList;
        return methodNode;
    }

    @Override
    public void process(EjectorContext ejectorContext) {
        ClassWrapper classWrapper = ejectorContext.getClassWrapper();
        MethodNode methodNode = ejectorContext.getMethodWrapper().methodNode;
        Frame<AbstractValue>[] frames = ejectorContext.getFrames();

        Map<MethodCallInfo, List<MethodInsnNode>> methodCalls = analyzeMethodCalls(methodNode, frames);
        if (methodCalls.isEmpty())
            return;

        Map<AbstractInsnNode, InsnList> patches = new HashMap<>();
        methodCalls.forEach((key, value) -> {
            MethodNode proxyMethod = createProxyMethod("_" + RandomUtils.getRandomInt(1, Integer.MAX_VALUE), key);
            int offset = key.opcode == INVOKESTATIC ? 0 : 1;

            Map<Integer, InsnList> proxyFixes = new HashMap<>();

            classWrapper.addMethod(proxyMethod);

            for (MethodInsnNode methodInsnNode : value) {
                int id = ejectorContext.getNextId();

                patches.put(methodInsnNode, ASMUtils.asList(
                        new LdcInsnNode(id),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, classWrapper.classNode.name, proxyMethod.name, proxyMethod.desc, false)
                ));


                InsnList proxyArgumentFix = new InsnList();
                Frame<AbstractValue> frame = frames[methodNode.instructions.indexOf(methodInsnNode)];

                Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);

                int variable = 0;
                for (int i = 0; i < argumentTypes.length; i++) {
                    AbstractValue argumentValue = frame.getStack(frame.getStackSize() - argumentTypes.length + i);
                    if (argumentValue.isConstant() && argumentValue.getUsages().size() == 1) {
                        AbstractInsnNode valueInsn = junkArguments ? ASMUtils.getRandomValue(argumentValue.getType()) : ASMUtils.getDefaultValue(argumentValue.getType());
                        patches.put(argumentValue.getInsnNode(), ASMUtils.singletonList(valueInsn));

                        proxyArgumentFix.add(argumentValue.getInsnNode().clone(null));
                        proxyArgumentFix.add(new VarInsnNode(ASMUtils.getVarOpcode(argumentValue.getType(), true), offset + variable));
                    }
                    variable += argumentTypes[i].getSize();
                }
                proxyFixes.put(id, proxyArgumentFix);
                ejectorContext.getCounter().incrementAndGet();

                if (!junkArguments)
                    continue;

                for (int k = 0; k < RandomUtils.getRandomInt(1, 5); k++) {
                    InsnList junkProxyArgumentFix = new InsnList();
                    int junkVariable = 0;
                    for (Type argumentType : argumentTypes) {
                        if (RandomUtils.getRandomBoolean()) {
                            junkProxyArgumentFix.add(ASMUtils.getRandomValue(argumentType));
                            junkProxyArgumentFix.add(new VarInsnNode(ASMUtils.getVarOpcode(argumentType, true), offset + junkVariable));
                        }
                        junkVariable += argumentType.getSize();
                    }
                    proxyFixes.put(ejectorContext.getNextId(), junkProxyArgumentFix);
                }
            }

            int idVariable = getLastArgumentVar(proxyMethod);

            InsnList proxyFix = new InsnList();
            LabelNode end = new LabelNode();

            ArrayList<Integer> keys = new ArrayList<>(proxyFixes.keySet());
            Collections.shuffle(keys);

            keys.forEach(id -> {
                int xorKey = RandomUtils.getRandomInt();

                InsnList insnList = proxyFixes.get(id);
                proxyFix.add(new VarInsnNode(Opcodes.ILOAD, idVariable));
                proxyFix.add(new LdcInsnNode(xorKey));
                proxyFix.add(new InsnNode(IXOR));
                proxyFix.add(new LdcInsnNode(id ^ xorKey));
                LabelNode labelNode = new LabelNode();
                proxyFix.add(new JumpInsnNode(Opcodes.IF_ICMPNE, labelNode));

                proxyFix.add(insnList);
                proxyFix.add(new JumpInsnNode(Opcodes.GOTO, end));
                proxyFix.add(labelNode);
            });

            proxyFix.add(end);
            proxyMethod.instructions.insert(proxyFix);
        });

        patches.forEach((abstractInsnNode, insnList) -> {
            methodNode.instructions.insertBefore(abstractInsnNode, insnList);
            methodNode.instructions.remove(abstractInsnNode);
        });
    }

    private int getLastArgumentVar(MethodNode methodNode) {
        Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
        int var = 0;
        for (int i = 0; i < argumentTypes.length - 1; i++) {
            var += argumentTypes[i].getSize();
        }
        return var;
    }

    private static class MethodCallInfo {
        private final String owner;
        private final boolean isInterface;
        private final int opcode;
        private final String name;
        private final String desc;

        MethodCallInfo(String owner, boolean isInterface, int opcode, String name, String desc) {
            this.owner = owner;
            this.isInterface = isInterface;
            this.opcode = opcode;
            this.name = name;
            this.desc = desc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodCallInfo that = (MethodCallInfo) o;
            return isInterface == that.isInterface &&
                    opcode == that.opcode &&
                    Objects.equals(owner, that.owner) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(desc, that.desc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, isInterface, opcode, name, desc);
        }
    }
}

package me.itzsomebody.radon.transformers.obfuscators.ejector.phases;

import me.itzsomebody.radon.Logger;
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

        MethodNode methodNode = new MethodNode(ACC_PRIVATE + ACC_STATIC, name, Type.getMethodDescriptor(returnType, arguments.toArray(new Type[0])),
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

        Map<AbstractInsnNode, List<AbstractInsnNode>> patches = new HashMap<>();

        methodCalls.forEach((key, value) -> {
            MethodNode proxyMethod = createProxyMethod("_" + RandomUtils.getRandomInt(1, Integer.MAX_VALUE), key);
            classWrapper.addMethod(proxyMethod);

            for (MethodInsnNode methodInsnNode : value) {
                int id = ejectorContext.getNextId();

                patches.put(methodInsnNode, Arrays.asList(
                        new LdcInsnNode(id),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, classWrapper.classNode.name, proxyMethod.name, proxyMethod.desc, false)
                ));

                Frame<AbstractValue> frame = frames[methodNode.instructions.indexOf(methodInsnNode)];

                Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);

                int offset = methodInsnNode.getOpcode() == INVOKESTATIC ? 0 : 1;
                int idVariable = 0;
                for (Type argumentType : argumentTypes) {
                    idVariable += argumentType.getSize();
                }

                int variable = 0;
                for (int i = 0; i < argumentTypes.length; i++) {
                    AbstractValue argumentValue = frame.getStack(frame.getStackSize() - argumentTypes.length + i);
                    if (argumentValue.isConstant() && argumentValue.getUsages().size() == 1) {
                        patches.put(argumentValue.getInsnNode(), Collections.singletonList(ASMUtils.getDefaultValue(argumentValue.getType())));

                        InsnList proxyArgumentFix = new InsnList();


                        proxyArgumentFix.add(new VarInsnNode(Opcodes.ILOAD, offset + idVariable));
                        proxyArgumentFix.add(new LdcInsnNode(id));

                        LabelNode labelNode = new LabelNode();
                        proxyArgumentFix.add(new JumpInsnNode(Opcodes.IF_ICMPNE, labelNode));

                        proxyArgumentFix.add(argumentValue.getInsnNode().clone(null));
                        proxyArgumentFix.add(new VarInsnNode(ASMUtils.getVarOpcode(argumentValue.getType(), true), offset + variable));

                        proxyArgumentFix.add(labelNode);

                        proxyMethod.instructions.insert(proxyArgumentFix);
                    }
                    variable += argumentTypes[i].getSize();
                }

                ejectorContext.getCounter().incrementAndGet();
            }
        });

        patches.forEach((abstractInsnNode, abstractInsnNodes) -> {
            InsnList insnList = new InsnList();
            abstractInsnNodes.forEach(insnList::add);

            methodNode.instructions.insertBefore(abstractInsnNode, insnList);
            methodNode.instructions.remove(abstractInsnNode);
        });
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

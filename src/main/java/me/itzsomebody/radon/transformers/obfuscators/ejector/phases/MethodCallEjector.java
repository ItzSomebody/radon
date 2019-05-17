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

public final class MethodCallEjector extends AbstractEjectPhase {

    public MethodCallEjector(EjectorContext ejectorContext) {
        super(ejectorContext);
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

        MethodNode methodNode = new MethodNode(getRandomAccess(), name, Type.getMethodDescriptor(returnType, arguments.toArray(new Type[0])),
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

    private static int getLastArgumentVar(MethodNode methodNode) {
        Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
        int var = 0;
        for (int i = 0; i < argumentTypes.length - 1; i++) {
            var += argumentTypes[i].getSize();
        }
        return var;
    }

    private Map<Integer, InsnList> createJunkArguments(Type[] argumentTypes, int offset) {
        Map<Integer, InsnList> junkArguments = new HashMap<>();

        for (int k = 0; k < getJunkArgumentCount(); k++) {
            InsnList junkProxyArgumentFix = new InsnList();
            int junkVariable = 0;
            for (Type argumentType : argumentTypes) {
                if (RandomUtils.getRandomBoolean()) {
                    junkProxyArgumentFix.add(ASMUtils.getRandomValue(argumentType));
                    junkProxyArgumentFix.add(new VarInsnNode(ASMUtils.getVarOpcode(argumentType, true), offset + junkVariable));
                }
                junkVariable += argumentType.getSize();
            }
            junkArguments.put(ejectorContext.getNextId(), junkProxyArgumentFix);
        }
        return junkArguments;
    }

    @Override
    public void process(MethodWrapper methodWrapper, Frame<AbstractValue>[] frames) {
        ClassWrapper classWrapper = ejectorContext.getClassWrapper();
        MethodNode methodNode = methodWrapper.getMethodNode();

        Map<MethodCallInfo, List<MethodInsnNode>> methodCalls = analyzeMethodCalls(methodNode, frames);
        if (methodCalls.isEmpty())
            return;
        methodWrapper.getMethodNode().maxStack++;

        Map<AbstractInsnNode, InsnList> patches = new HashMap<>();
        methodCalls.forEach((key, value) -> {
            MethodNode proxyMethod = createProxyMethod(getProxyMethodName(methodNode), key);
            int offset = key.opcode == INVOKESTATIC ? 0 : 1;

            Map<Integer, InsnList> proxyFixes = new HashMap<>();

            classWrapper.addMethod(proxyMethod);

            for (MethodInsnNode methodInsnNode : value) {
                int id = ejectorContext.getNextId();

                patches.put(methodInsnNode, ASMUtils.asList(
                        new LdcInsnNode(id),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, classWrapper.getName(), proxyMethod.name, proxyMethod.desc, false)
                ));


                InsnList proxyArgumentFix = new InsnList();
                Frame<AbstractValue> frame = frames[methodNode.instructions.indexOf(methodInsnNode)];

                Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);

                int variable = 0;
                for (int i = 0; i < argumentTypes.length; i++) {
                    Type argumentType = argumentTypes[i];
                    AbstractValue argumentValue = frame.getStack(frame.getStackSize() - argumentTypes.length + i);
                    if (argumentValue.isConstant() && argumentValue.getUsages().size() == 1) {
                        AbstractInsnNode valueInsn = ejectorContext.isJunkArguments() ? ASMUtils.getRandomValue(argumentType) : ASMUtils.getDefaultValue(argumentType);
                        patches.put(argumentValue.getInsnNode(), ASMUtils.singletonList(valueInsn));

                        proxyArgumentFix.add(argumentValue.getInsnNode().clone(null));
                        proxyArgumentFix.add(new VarInsnNode(ASMUtils.getVarOpcode(argumentType, true), offset + variable));
                    }
                    variable += argumentTypes[i].getSize();
                }
                proxyFixes.put(id, proxyArgumentFix);
                ejectorContext.getCounter().incrementAndGet();

                if (ejectorContext.isJunkArguments()) {
                    proxyFixes.putAll(createJunkArguments(argumentTypes, offset));
                }
            }

            int idVariable = getLastArgumentVar(proxyMethod);
            insertFixes(proxyMethod, proxyFixes, idVariable);
        });

        patches.forEach((abstractInsnNode, insnList) -> {
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

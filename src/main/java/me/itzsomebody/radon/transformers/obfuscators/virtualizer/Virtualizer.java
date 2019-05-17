/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.transformers.obfuscators.virtualizer;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.vm.Instruction;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMContext;
import me.itzsomebody.vm.VMTryCatch;
import me.itzsomebody.vm.datatypes.JObject;
import me.itzsomebody.vm.datatypes.JWrapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Translates Java bytecode into a custom bytecode instruction set. Piece of trash and hackiest thing ever.
 * TODO: clean everything up
 * FIXME: sorta broken
 *
 * @author ItzSomebody
 */
public class Virtualizer extends Transformer implements VMOpcodes {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        StubCreator stubCreator = new StubCreator();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !"<init>".equals(methodWrapper.getOriginalName())
                        && !excluded(methodWrapper)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.getMethodNode();

                    int leeway = getSizeLeeway(methodNode);
                    if (leeway <= 30000 || !canProtect(methodNode.instructions)) // Virtualization of big method = mega bad
                        return;

                    VirtualizerResult result = translate(methodNode, counter.get());
                    stubCreator.addInstructionList(result.getVMInstructions());
                    methodNode.instructions = result.getVMCall();
                    methodNode.localVariables = null;
                    methodNode.tryCatchBlocks = null;

                    counter.incrementAndGet();
                }));

        try {
            getResources().put("radon.vm", stubCreator.createStub());
            shadeVMRuntime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RadonException();
        }

        Main.info("Virtualized " + counter.get() + " methods");
    }

    private static boolean canProtect(InsnList insnList) {
        return Stream.of(insnList.toArray()).noneMatch(insn -> insn.getOpcode() == INVOKEDYNAMIC
                || (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof Handle)
                || insn instanceof MultiANewArrayInsnNode
                || insn instanceof LookupSwitchInsnNode
                || insn instanceof TableSwitchInsnNode);
    }

    private void shadeVMRuntime() throws Exception {
        File file = new File(Virtualizer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();

            if (zipEntry.getName().startsWith("me/itzsomebody/vm") && zipEntry.getName().endsWith(".class"))
                try (InputStream in = zip.getInputStream(zipEntry)) {
                    ClassReader cr = new ClassReader(in);
                    ClassWrapper cw = new ClassWrapper(cr, false);
                    getClasses().put(cw.getName(), cw);
                    getClassPath().put(cw.getName(), cw);
                }
        }
    }

    private VirtualizerResult translate(MethodNode methodNode, int offset) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        Map<LabelNode, Instruction> targetMap = new HashMap<>();

        Stream.of(methodNode.instructions.toArray()).forEach(insn -> {
            switch (insn.getOpcode()) {
                case NOP:
                    instructions.add(new Instruction(VM_NOP, new Object[0]));
                    break;
                case ACONST_NULL:
                case NEW:
                    instructions.add(new Instruction(VM_NULL_PUSH, new Object[0]));
                    break;
                case ICONST_M1:
                case ICONST_0:
                case ICONST_1:
                case ICONST_2:
                case ICONST_3:
                case ICONST_4:
                case ICONST_5:
                    instructions.add(new Instruction(VM_INT_PUSH, new Object[]{insn.getOpcode() - 3}));
                    break;
                case LCONST_0:
                case LCONST_1:
                    instructions.add(new Instruction(VM_LONG_PUSH, new Object[]{insn.getOpcode() - 9}));
                    break;
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                    instructions.add(new Instruction(VM_FLOAT_PUSH, new Object[]{insn.getOpcode() - 11}));
                    break;
                case DCONST_0:
                case DCONST_1:
                    instructions.add(new Instruction(VM_DOUBLE_PUSH, new Object[]{insn.getOpcode() - 14}));
                    break;
                case BIPUSH:
                case SIPUSH:
                    instructions.add(new Instruction(VM_INT_PUSH, new Object[]{((IntInsnNode) insn).operand}));
                    break;
                case LDC:
                    Object cst = ((LdcInsnNode) insn).cst;

                    if (cst instanceof Integer)
                        instructions.add(new Instruction(VM_INT_PUSH, new Object[]{cst}));
                    else if (cst instanceof Long)
                        instructions.add(new Instruction(VM_LONG_PUSH, new Object[]{cst}));
                    else if (cst instanceof Float)
                        instructions.add(new Instruction(VM_FLOAT_PUSH, new Object[]{cst}));
                    else if (cst instanceof Double)
                        instructions.add(new Instruction(VM_DOUBLE_PUSH, new Object[]{cst}));
                    else if (cst instanceof String || cst instanceof Type)
                        instructions.add(new Instruction(VM_OBJ_PUSH, new Object[]{cst}));
                    break;
                case ILOAD:
                case LLOAD:
                case FLOAD:
                case DLOAD:
                case ALOAD:
                    instructions.add(new Instruction(VM_LOAD, new Object[]{((VarInsnNode) insn).var}));
                    break;
                case IALOAD:
                case LALOAD:
                case FALOAD:
                case DALOAD:
                case BALOAD:
                case CALOAD:
                case SALOAD:
                    instructions.add(new Instruction(VM_ARR_LOAD, new Object[]{0}));
                    break;
                case AALOAD:
                    instructions.add(new Instruction(VM_ARR_LOAD, new Object[]{1}));
                    break;
                case ISTORE:
                case LSTORE:
                case FSTORE:
                case DSTORE:
                case ASTORE:
                    instructions.add(new Instruction(VM_STORE, new Object[]{((VarInsnNode) insn).var}));
                    break;
                case IASTORE:
                case LASTORE:
                case FASTORE:
                case DASTORE:
                case AASTORE:
                case BASTORE:
                case CASTORE:
                case SASTORE:
                    instructions.add(new Instruction(VM_ARR_STORE, new Object[0]));
                    break;
                case POP:
                    instructions.add(new Instruction(VM_POP, new Object[0]));
                    break;
                case POP2:
                    instructions.add(new Instruction(VM_POP2, new Object[0]));
                    break;
                case DUP:
                case DUP_X1:
                case DUP_X2:
                case DUP2:
                case DUP2_X1:
                case DUP2_X2:
                    instructions.add(new Instruction(VM_DUP, new Object[]{insn.getOpcode() - 89}));
                    break;
                case SWAP:
                    instructions.add(new Instruction(VM_SWAP, new Object[0]));
                    break;
                case IADD:
                case LADD:
                case FADD:
                case DADD:
                    instructions.add(new Instruction(VM_ADD, new Object[0]));
                    break;
                case ISUB:
                case LSUB:
                case FSUB:
                case DSUB:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    break;
                case IMUL:
                case LMUL:
                case FMUL:
                case DMUL:
                    instructions.add(new Instruction(VM_MUL, new Object[0]));
                    break;
                case IDIV:
                case LDIV:
                case FDIV:
                case DDIV:
                    instructions.add(new Instruction(VM_DIV, new Object[0]));
                    break;
                case IREM:
                case LREM:
                case FREM:
                case DREM:
                    instructions.add(new Instruction(VM_MOD, new Object[0]));
                    break;
                case INEG:
                case LNEG:
                case FNEG:
                case DNEG:
                    instructions.add(new Instruction(VM_INT_PUSH, new Object[]{-1}));
                    instructions.add(new Instruction(VM_MUL, new Object[0]));
                    break;
                case ISHL:
                case LSHL:
                    instructions.add(new Instruction(VM_SHL, new Object[0]));
                    break;
                case ISHR:
                case LSHR:
                    instructions.add(new Instruction(VM_SHR, new Object[0]));
                    break;
                case IUSHR:
                case LUSHR:
                    instructions.add(new Instruction(VM_USHR, new Object[0]));
                    break;
                case IAND:
                case LAND:
                    instructions.add(new Instruction(VM_AND, new Object[0]));
                    break;
                case IOR:
                case LOR:
                    instructions.add(new Instruction(VM_OR, new Object[0]));
                    break;
                case IXOR:
                case LXOR:
                    instructions.add(new Instruction(VM_XOR, new Object[0]));
                    break;
                case IINC:
                    IincInsnNode inc = (IincInsnNode) insn;
                    instructions.add(new Instruction(VM_INC, new Object[]{inc.var, inc.incr}));
                    break;
                case I2L:
                case I2F:
                case I2D:
                case L2I:
                case L2F:
                case L2D:
                case F2I:
                case F2L:
                case F2D:
                case D2I:
                case D2L:
                case D2F:
                case I2B:
                case I2C:
                case I2S:
                    instructions.add(new Instruction(VM_PRIM_CAST, new Object[]{insn.getOpcode() - 133}));
                    break;
                case LCMP:
                    instructions.add(new Instruction(VM_LCMP, new Object[0]));
                    break;
                case FCMPL:
                    instructions.add(new Instruction(VM_FCMPL, new Object[0]));
                    break;
                case FCMPG:
                    instructions.add(new Instruction(VM_FCMPG, new Object[0]));
                    break;
                case DCMPL:
                    instructions.add(new Instruction(VM_DCMPL, new Object[0]));
                    break;
                case DCMPG:
                    instructions.add(new Instruction(VM_DCMPG, new Object[0]));
                    break;
                case IFEQ:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JZ, new Object[0]));
                    break;
                case IFNE:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JNZ, new Object[0]));
                    break;
                case IFLT:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JLT, new Object[0]));
                    break;
                case IFGE:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JGE, new Object[0]));
                    break;
                case IFGT:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JGT, new Object[0]));
                    break;
                case IFLE:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JLE, new Object[0]));
                    break;
                case IF_ICMPEQ:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JZ, new Object[0]));
                    break;
                case IF_ICMPNE:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JNZ, new Object[0]));
                    break;
                case IF_ICMPLT:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    instructions.add(new Instruction(VM_JLT, new Object[]{((JumpInsnNode) insn).label}));
                    break;
                case IF_ICMPGE:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JGE, new Object[0]));
                    break;
                case IF_ICMPGT:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JGT, new Object[0]));
                    break;
                case IF_ICMPLE:
                    instructions.add(new Instruction(VM_SUB, new Object[0]));
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JLE, new Object[0]));
                    break;
                case IF_ACMPEQ:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JEQ, new Object[0]));
                    break;
                case IF_ACMPNE:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JNE, new Object[0]));
                    break;
                case GOTO:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JMP, new Object[0]));
                    break;
                case JSR:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JSR, new Object[0]));
                    break;
                case RET:
                    instructions.add(new Instruction(VM_RET, new Object[]{((VarInsnNode) insn).var}));
                    break;
                case TABLESWITCH:
                case LOOKUPSWITCH:
                    // todo;
                    throw new RadonException("Tableswitch / lookupswitch not supported yet");
                case IRETURN:
                case LRETURN:
                case FRETURN:
                case DRETURN:
                case ARETURN:
                    instructions.add(new Instruction(VM_KILL, new Object[0]));
                    break;
                case RETURN:
                    instructions.add(new Instruction(VM_NULL_PUSH, new Object[0]));
                    instructions.add(new Instruction(VM_KILL, new Object[0]));
                    break;
                case GETSTATIC:
                case PUTSTATIC:
                case GETFIELD:
                case PUTFIELD:
                    FieldInsnNode fin = (FieldInsnNode) insn;
                    int opcode;
                    switch (insn.getOpcode()) {
                        case GETSTATIC:
                            opcode = VM_STATIC_GET;
                            break;
                        case PUTSTATIC:
                            opcode = VM_STATIC_SET;
                            break;
                        case GETFIELD:
                            opcode = VM_VIRT_GET;
                            break;
                        default:
                            opcode = VM_VIRT_SET;
                            break;
                    }

                    Type fieldType = Type.getType(fin.desc);

                    instructions.add(new Instruction(opcode, new Object[]
                            {
                                    fin.owner.replace('/', '.'),
                                    fin.name,
                                    (fieldType.getSort() == Type.ARRAY) ? fieldType.getInternalName().replace('/', '.') : fieldType.getClassName()
                            }));
                    break;
                case INVOKEVIRTUAL:
                case INVOKESPECIAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                    MethodInsnNode min = (MethodInsnNode) insn;
                    int mOpcode;
                    switch (insn.getOpcode()) {
                        case INVOKESPECIAL:
                            mOpcode = "<init>".equals(min.name) ? VM_INSTANTIATE : VM_VIRT_CALL;
                            break;
                        case INVOKESTATIC:
                            mOpcode = VM_STATIC_CALL;
                            break;
                        default:
                            mOpcode = VM_VIRT_CALL;
                            break;
                    }

                    if (mOpcode == VM_INSTANTIATE)
                        instructions.add(new Instruction(mOpcode, new Object[]
                                {
                                        min.owner.replace('/', '.'),
                                        getVMMethodDesc(min.desc)
                                }));
                    else
                        instructions.add(new Instruction(mOpcode, new Object[]
                                {
                                        min.owner.replace('/', '.'),
                                        min.name,
                                        getVMMethodDesc(min.desc)
                                }));
                    break;
                case INVOKEDYNAMIC:
                    throw new RadonException("Invokedynamic not supported");
                case NEWARRAY:
                    IntInsnNode newArray = (IntInsnNode) insn;
                    String arrayType;
                    switch (newArray.operand) {
                        case T_BOOLEAN:
                            arrayType = "boolean";
                            break;
                        case T_CHAR:
                            arrayType = "char";
                            break;
                        case T_FLOAT:
                            arrayType = "float";
                            break;
                        case T_DOUBLE:
                            arrayType = "double";
                            break;
                        case T_BYTE:
                            arrayType = "byte";
                            break;
                        case T_SHORT:
                            arrayType = "short";
                            break;
                        case T_INT:
                            arrayType = "int";
                            break;
                        case T_LONG:
                            arrayType = "long";
                            break;
                        default:
                            throw new RadonException("Bad NEWARRAY type: " + newArray.operand);
                    }
                    instructions.add(new Instruction(VM_NEW_ARR, new Object[]{arrayType}));
                    break;
                case ANEWARRAY:
                    instructions.add(new Instruction(VM_NEW_ARR, new Object[]{((TypeInsnNode) insn).desc.replace('/', '.')}));
                    break;
                case ARRAYLENGTH:
                    instructions.add(new Instruction(VM_ARR_LENGTH, new Object[0]));
                    break;
                case ATHROW:
                    instructions.add(new Instruction(VM_THROW, new Object[0]));
                    break;
                case CHECKCAST:
                    instructions.add(new Instruction(VM_CHECKCAST, new Object[]{((TypeInsnNode) insn).desc.replace('/', '.')}));
                    break;
                case INSTANCEOF:
                    instructions.add(new Instruction(VM_INSTANCE_OF, new Object[]{((TypeInsnNode) insn).desc.replace('/', '.')}));
                    break;
                case MONITORENTER:
                    instructions.add(new Instruction(VM_MONITOR, new Object[]{0}));
                    break;
                case MONITOREXIT:
                    instructions.add(new Instruction(VM_MONITOR, new Object[]{1}));
                    break;
                case MULTIANEWARRAY:
                    throw new RadonException("MULTINEWARRAY not supported");
                case IFNULL:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JN, new Object[0]));
                    break;
                case IFNONNULL:
                    instructions.add(new Instruction(VM_NOP, new Object[]{((JumpInsnNode) insn).label}));
                    instructions.add(new Instruction(VM_JNN, new Object[0]));
                    break;
                default:
                    Instruction nopInstruction = new Instruction(VM_NOP, new Object[0]);

                    if (insn instanceof LabelNode) {
                        nopInstruction.setOperands(new Object[]{instructions.size()});
                        targetMap.put((LabelNode) insn, nopInstruction);
                    }

                    instructions.add(nopInstruction);
            }
        });

        // fixme: hacky
        instructions.stream().filter(instruction -> instruction.getOperands().length == 1
                && instruction.getOperands()[0] instanceof LabelNode).forEach(instruction -> {
            LabelNode label = (LabelNode) instruction.getOperands()[0];
            Instruction nop = targetMap.get(label);
            instructions.set(instructions.indexOf(instruction), new Instruction(VM_INT_PUSH, new Object[]{nop.getOperands()[0]}));
        });

        InsnList vmCall = new InsnList();
        vmCall.add(new TypeInsnNode(NEW, Type.getType(VM.class).getInternalName()));
        vmCall.add(new InsnNode(DUP));
        vmCall.add(new TypeInsnNode(NEW, Type.getType(VMContext.class).getInternalName()));
        vmCall.add(new InsnNode(DUP));
        vmCall.add(ASMUtils.getNumberInsn((methodNode.maxStack + 1) << 1));
        vmCall.add(ASMUtils.getNumberInsn((methodNode.maxLocals + 1) << 1));
        vmCall.add(ASMUtils.getNumberInsn(offset));
        if (methodNode.tryCatchBlocks != null && !methodNode.tryCatchBlocks.isEmpty()) {
            vmCall.add(ASMUtils.getNumberInsn(methodNode.tryCatchBlocks.size()));
            vmCall.add(new TypeInsnNode(ANEWARRAY, Type.getType(VMTryCatch.class).getInternalName()));
            IntStream.range(0, methodNode.tryCatchBlocks.size()).forEach(i -> {
                TryCatchBlockNode tcbn = methodNode.tryCatchBlocks.get(i);
                vmCall.add(new InsnNode(DUP));
                vmCall.add(ASMUtils.getNumberInsn(i));
                vmCall.add(new TypeInsnNode(NEW, Type.getType(VMTryCatch.class).getInternalName()));
                vmCall.add(new InsnNode(DUP));
                vmCall.add(ASMUtils.getNumberInsn((Integer) targetMap.get(tcbn.start).getOperands()[0]));
                vmCall.add(ASMUtils.getNumberInsn((Integer) targetMap.get(tcbn.end).getOperands()[0]));
                vmCall.add(ASMUtils.getNumberInsn((Integer) targetMap.get(tcbn.handler).getOperands()[0]));
                if (tcbn.type == null)
                    vmCall.add(new InsnNode(ACONST_NULL));
                else
                    vmCall.add(new LdcInsnNode(tcbn.type));
                vmCall.add(new MethodInsnNode(INVOKESPECIAL, Type.getType(VMTryCatch.class).getInternalName(), "<init>", "(IIILjava/lang/String;)V", false));
                vmCall.add(new InsnNode(AASTORE));
            });
            vmCall.add(new MethodInsnNode(INVOKESPECIAL, Type.getType(VMContext.class).getInternalName(), "<init>", "(III[L" + Type.getType(VMTryCatch.class).getInternalName() + ";)V", false));
        } else
            vmCall.add(new MethodInsnNode(INVOKESPECIAL, Type.getType(VMContext.class).getInternalName(), "<init>", "(III)V", false));
        Type[] argTypes = Type.getArgumentTypes(methodNode.desc);
        if (argTypes.length > 0) {
            int index = (Modifier.isStatic(methodNode.access)) ? 0 : 1;

            for (Type type : argTypes) {
                vmCall.add(new InsnNode(DUP));
                switch (type.getSort()) {
                    case Type.BOOLEAN:
                    case Type.CHAR:
                    case Type.BYTE:
                    case Type.SHORT:
                    case Type.INT:
                        vmCall.add(new VarInsnNode(ILOAD, index));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, Type.getType(JWrapper.class).getInternalName(), "fromPrimitive", "(Ljava/lang/Object;)L" + Type.getType(JWrapper.class).getInternalName() + ";", false));
                        vmCall.add(ASMUtils.getNumberInsn(index));
                        vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(VMContext.class).getInternalName(), "initRegister", "(L" + Type.getType(JWrapper.class).getInternalName() + ";I)V", false));
                        index++;
                        break;
                    case Type.FLOAT:
                        vmCall.add(new VarInsnNode(FLOAD, index));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, Type.getType(JWrapper.class).getInternalName(), "fromPrimitive", "(Ljava/lang/Object;)L" + Type.getType(JWrapper.class).getInternalName() + ";", false));
                        vmCall.add(ASMUtils.getNumberInsn(index));
                        vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(VMContext.class).getInternalName(), "initRegister", "(L" + Type.getType(JWrapper.class).getInternalName() + ";I)V", false));
                        index++;
                        break;
                    case Type.LONG:
                        vmCall.add(new VarInsnNode(LLOAD, index));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, Type.getType(JWrapper.class).getInternalName(), "fromPrimitive", "(Ljava/lang/Object;)L" + Type.getType(JWrapper.class).getInternalName() + ";", false));
                        vmCall.add(ASMUtils.getNumberInsn(index));
                        vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(VMContext.class).getInternalName(), "initRegister", "(L" + Type.getType(JWrapper.class).getInternalName() + ";I)V", false));
                        index += 2;
                        break;
                    case Type.DOUBLE:
                        vmCall.add(new VarInsnNode(DLOAD, index));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Integer;", false));
                        vmCall.add(new MethodInsnNode(INVOKESTATIC, Type.getType(JWrapper.class).getInternalName(), "fromPrimitive", "(Ljava/lang/Object;)L" + Type.getType(JWrapper.class).getInternalName() + ";", false));
                        vmCall.add(ASMUtils.getNumberInsn(index));
                        vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(VMContext.class).getInternalName(), "initRegister", "(L" + Type.getType(JWrapper.class).getInternalName() + ";I)V", false));
                        index += 2;
                        break;
                    default:
                        vmCall.add(new TypeInsnNode(NEW, Type.getType(JObject.class).getInternalName()));
                        vmCall.add(new InsnNode(DUP));
                        vmCall.add(new VarInsnNode(ALOAD, index));
                        vmCall.add(new MethodInsnNode(INVOKESPECIAL, Type.getType(JObject.class).getInternalName(), "<init>", "(Ljava/lang/Object;)V", false));
                        vmCall.add(ASMUtils.getNumberInsn(index));
                        vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(VMContext.class).getInternalName(), "initRegister", "(L" + Type.getType(JWrapper.class).getInternalName() + ";I)V", false));
                        index++;
                        break;
                }
            }
        }
        vmCall.add(new MethodInsnNode(INVOKESPECIAL, Type.getType(VM.class).getInternalName(), "<init>", "(L" + Type.getType(VMContext.class).getInternalName() + ";)V", false));
        vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(VM.class).getInternalName(), "execute", "()L" + Type.getType(JWrapper.class).getInternalName() + ";", false));
        switch (Type.getReturnType(methodNode.desc).getSort()) {
            case Type.VOID:
                vmCall.add(new InsnNode(POP));
                vmCall.add(new InsnNode(RETURN));
                break;
            case Type.BOOLEAN:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asBool", "()Z", false));
                vmCall.add(new InsnNode(IRETURN));
                break;
            case Type.CHAR:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asChar", "()C", false));
                vmCall.add(new InsnNode(IRETURN));
                break;
            case Type.BYTE:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asByte", "()B", false));
                vmCall.add(new InsnNode(IRETURN));
                break;
            case Type.SHORT:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asShort", "()S", false));
                vmCall.add(new InsnNode(IRETURN));
                break;
            case Type.INT:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asInt", "()I", false));
                vmCall.add(new InsnNode(IRETURN));
                break;
            case Type.FLOAT:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asFloat", "()F", false));
                vmCall.add(new InsnNode(FRETURN));
                break;
            case Type.LONG:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asLong", "()J", false));
                vmCall.add(new InsnNode(LRETURN));
                break;
            case Type.DOUBLE:
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asDouble", "()D", false));
                vmCall.add(new InsnNode(DRETURN));
                break;
            default:
                Type returnType = Type.getReturnType(methodNode.desc);
                vmCall.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getType(JWrapper.class).getInternalName(), "asObj", "()Ljava/lang/Object;", false));
                vmCall.add(new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
                vmCall.add(new InsnNode(ARETURN));
                break;
        }

        return new VirtualizerResult(instructions, vmCall);
    }

    private static String getVMMethodDesc(String desc) {
        Type[] types = Type.getArgumentTypes(desc);
        StringBuilder sb = new StringBuilder();

        if (types.length == 0)
            sb.append("\u0000\u0000\u0000");
        else
            Arrays.stream(types).forEach(type -> {
                if (type.getSort() == Type.ARRAY)
                    sb.append(type.getInternalName().replace('/', '.'));
                else
                    sb.append(type.getClassName());

                sb.append("\u0001\u0001");
            });

        return sb.toString();
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.VIRTUALIZER;
    }

    @Override
    public String getName() {
        return "Virtualizer";
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // Not needed
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.VIRTUALIZER + " expects a boolean");
    }

    private class VirtualizerResult {
        private final ArrayList<Instruction> vmInstructions;
        private final InsnList vmCall;

        private VirtualizerResult(ArrayList<Instruction> vmInstructions, InsnList vmCall) {
            this.vmInstructions = vmInstructions;
            this.vmCall = vmCall;
        }

        private ArrayList<Instruction> getVMInstructions() {
            return vmInstructions;
        }

        private InsnList getVMCall() {
            return vmCall;
        }
    }
}

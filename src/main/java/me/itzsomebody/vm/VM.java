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

package me.itzsomebody.vm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import me.itzsomebody.vm.datatypes.JObject;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;
import me.itzsomebody.vm.handlers.Add;
import me.itzsomebody.vm.handlers.And;
import me.itzsomebody.vm.handlers.ArrLength;
import me.itzsomebody.vm.handlers.ArrLoad;
import me.itzsomebody.vm.handlers.ArrStore;
import me.itzsomebody.vm.handlers.Checkcast;
import me.itzsomebody.vm.handlers.Dcmpg;
import me.itzsomebody.vm.handlers.Dcmpl;
import me.itzsomebody.vm.handlers.Div;
import me.itzsomebody.vm.handlers.DoublePush;
import me.itzsomebody.vm.handlers.Dup;
import me.itzsomebody.vm.handlers.Fcmpg;
import me.itzsomebody.vm.handlers.Fcmpl;
import me.itzsomebody.vm.handlers.FloatPush;
import me.itzsomebody.vm.handlers.Handler;
import me.itzsomebody.vm.handlers.Inc;
import me.itzsomebody.vm.handlers.Instanceof;
import me.itzsomebody.vm.handlers.Instantiate;
import me.itzsomebody.vm.handlers.IntPush;
import me.itzsomebody.vm.handlers.Jeq;
import me.itzsomebody.vm.handlers.Jge;
import me.itzsomebody.vm.handlers.Jgt;
import me.itzsomebody.vm.handlers.Jle;
import me.itzsomebody.vm.handlers.Jlt;
import me.itzsomebody.vm.handlers.Jmp;
import me.itzsomebody.vm.handlers.Jn;
import me.itzsomebody.vm.handlers.Jne;
import me.itzsomebody.vm.handlers.Jnn;
import me.itzsomebody.vm.handlers.Jnz;
import me.itzsomebody.vm.handlers.Jsr;
import me.itzsomebody.vm.handlers.Jz;
import me.itzsomebody.vm.handlers.Kill;
import me.itzsomebody.vm.handlers.Lcmp;
import me.itzsomebody.vm.handlers.Load;
import me.itzsomebody.vm.handlers.LongPush;
import me.itzsomebody.vm.handlers.Mod;
import me.itzsomebody.vm.handlers.Monitor;
import me.itzsomebody.vm.handlers.Mul;
import me.itzsomebody.vm.handlers.Neg;
import me.itzsomebody.vm.handlers.NewArr;
import me.itzsomebody.vm.handlers.Nop;
import me.itzsomebody.vm.handlers.NullPush;
import me.itzsomebody.vm.handlers.ObjPush;
import me.itzsomebody.vm.handlers.Or;
import me.itzsomebody.vm.handlers.Pop;
import me.itzsomebody.vm.handlers.Pop2;
import me.itzsomebody.vm.handlers.PrimCast;
import me.itzsomebody.vm.handlers.Ret;
import me.itzsomebody.vm.handlers.Shl;
import me.itzsomebody.vm.handlers.Shr;
import me.itzsomebody.vm.handlers.StaticCall;
import me.itzsomebody.vm.handlers.StaticGet;
import me.itzsomebody.vm.handlers.StaticSet;
import me.itzsomebody.vm.handlers.Store;
import me.itzsomebody.vm.handlers.Sub;
import me.itzsomebody.vm.handlers.Swap;
import me.itzsomebody.vm.handlers.Throw;
import me.itzsomebody.vm.handlers.Ushr;
import me.itzsomebody.vm.handlers.VirtCall;
import me.itzsomebody.vm.handlers.VirtGet;
import me.itzsomebody.vm.handlers.VirtSet;
import me.itzsomebody.vm.handlers.Xor;

import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_ADD;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_AND;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_ARR_LENGTH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_ARR_LOAD;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_ARR_STORE;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_CHECKCAST;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_DCMPG;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_DCMPL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_DIV;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_DOUBLE_PUSH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_DUP;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_FCMPG;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_FCMPL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_FLOAT_PUSH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_INC;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_INSTANCE_OF;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_INSTANTIATE;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_INT_PUSH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JEQ;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JGE;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JGT;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JLE;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JLT;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JMP;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JN;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JNE;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JNN;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JNZ;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JSR;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_JZ;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_KILL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_LCMP;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_LOAD;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_LONG_PUSH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_MOD;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_MONITOR;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_MUL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_NEG;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_NEW_ARR;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_NOP;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_NULL_PUSH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_OBJ_PUSH;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_OR;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_POP;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_POP2;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_PRIM_CAST;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_RET;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_SHL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_SHR;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_STATIC_CALL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_STATIC_GET;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_STATIC_SET;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_STORE;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_SUB;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_SWAP;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_THROW;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_USHR;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_VIRT_CALL;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_VIRT_GET;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_VIRT_SET;
import static me.itzsomebody.radon.transformers.obfuscators.virtualizer.VMOpcodes.VM_XOR;

public class VM {
    private static final Map<String, Method> METHOD_CACHE;
    private static final Map<String, Field> FIELD_CACHE;
    private static final Map<String, Constructor> CONSTRUCTOR_CACHE;
    private static final Handler[] HANDLERS;
    private static final Stub STUB;
    private final VMStack stack;
    private final JWrapper[] registers;
    private final Instruction[] instructions;
    private final VMTryCatch[] catches;
    private int pc;
    private boolean executing;

    static {
        METHOD_CACHE = new HashMap<>();
        FIELD_CACHE = new HashMap<>();
        CONSTRUCTOR_CACHE = new HashMap<>();
        try {
            STUB = new Stub();
        } catch (Exception e) {
            throw new VMException();
        }

        HANDLERS = new Handler[61];
        HANDLERS[VM_NULL_PUSH] = new NullPush();
        HANDLERS[VM_INT_PUSH] = new IntPush();
        HANDLERS[VM_LONG_PUSH] = new LongPush();
        HANDLERS[VM_FLOAT_PUSH] = new FloatPush();
        HANDLERS[VM_DOUBLE_PUSH] = new DoublePush();
        HANDLERS[VM_OBJ_PUSH] = new ObjPush();
        HANDLERS[VM_ADD] = new Add();
        HANDLERS[VM_SUB] = new Sub();
        HANDLERS[VM_MUL] = new Mul();
        HANDLERS[VM_DIV] = new Div();
        HANDLERS[VM_MOD] = new Mod();
        HANDLERS[VM_AND] = new And();
        HANDLERS[VM_OR] = new Or();
        HANDLERS[VM_XOR] = new Xor();
        HANDLERS[VM_SHL] = new Shl();
        HANDLERS[VM_SHR] = new Shr();
        HANDLERS[VM_USHR] = new Ushr();
        HANDLERS[VM_LOAD] = new Load();
        HANDLERS[VM_STORE] = new Store();
        HANDLERS[VM_ARR_LOAD] = new ArrLoad();
        HANDLERS[VM_ARR_STORE] = new ArrStore();
        HANDLERS[VM_POP] = new Pop();
        HANDLERS[VM_POP2] = new Pop2();
        HANDLERS[VM_DUP] = new Dup();
        HANDLERS[VM_SWAP] = new Swap();
        HANDLERS[VM_INC] = new Inc();
        HANDLERS[VM_PRIM_CAST] = new PrimCast();
        HANDLERS[VM_LCMP] = new Lcmp();
        HANDLERS[VM_FCMPL] = new Fcmpl();
        HANDLERS[VM_FCMPG] = new Fcmpg();
        HANDLERS[VM_DCMPL] = new Dcmpl();
        HANDLERS[VM_DCMPG] = new Dcmpg();
        HANDLERS[VM_JZ] = new Jz();
        HANDLERS[VM_JNZ] = new Jnz();
        HANDLERS[VM_JLT] = new Jlt();
        HANDLERS[VM_JLE] = new Jle();
        HANDLERS[VM_JGT] = new Jgt();
        HANDLERS[VM_JGE] = new Jge();
        HANDLERS[VM_JEQ] = new Jeq();
        HANDLERS[VM_JMP] = new Jmp();
        HANDLERS[VM_JSR] = new Jsr();
        HANDLERS[VM_RET] = new Ret();
        HANDLERS[VM_VIRT_GET] = new VirtGet();
        HANDLERS[VM_STATIC_GET] = new StaticGet();
        HANDLERS[VM_VIRT_SET] = new VirtSet();
        HANDLERS[VM_STATIC_SET] = new StaticSet();
        HANDLERS[VM_VIRT_CALL] = new VirtCall();
        HANDLERS[VM_STATIC_CALL] = new StaticCall();
        HANDLERS[VM_INSTANTIATE] = new Instantiate();
        HANDLERS[VM_NEW_ARR] = new NewArr();
        HANDLERS[VM_ARR_LENGTH] = new ArrLength();
        HANDLERS[VM_THROW] = new Throw();
        HANDLERS[VM_CHECKCAST] = new Checkcast();
        HANDLERS[VM_INSTANCE_OF] = new Instanceof();
        HANDLERS[VM_MONITOR] = new Monitor();
        HANDLERS[VM_JN] = new Jn();
        HANDLERS[VM_JNN] = new Jnn();
        HANDLERS[VM_NOP] = new Nop();
        HANDLERS[VM_KILL] = new Kill();
        HANDLERS[VM_NEG] = new Neg();
        HANDLERS[VM_JNE] = new Jne();
    }

    public VM(VMContext context) {
        this.stack = context.getStack();
        this.registers = context.getRegisters();
        this.instructions = STUB.instructions[context.getOffset()];
        this.catches = context.getCatches();
        this.pc = 0;
        this.executing = true;
    }

    public void push(JWrapper wrapper) {
        stack.push(wrapper);
    }

    public JWrapper pop() {
        return stack.pop();
    }

    public JWrapper loadRegister(int index) {
        return registers[index];
    }

    public void storeRegister(JWrapper wrapper, int index) {
        registers[index] = wrapper;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setExecuting(boolean executing) {
        this.executing = executing;
    }

    public JWrapper execute() throws Throwable {
        while (executing) {
            try {
                Instruction instruction = instructions[pc];

                Handler handler = HANDLERS[instruction.getOpcode()];
                handler.handle(this, instruction.getOperands());

                pc++;
            } catch (Throwable t) {
                boolean unhandled = true;

                if (catches != null)
                    for (int i = 0; i < catches.length; i++) {
                        VMTryCatch vmCatch = catches[i];

                        if ((vmCatch.getType() == null || Class.forName(vmCatch.getType()).isInstance(t))
                                && (pc >= vmCatch.getStartPc() && pc < vmCatch.getEndPc())) {
                            stack.clear();
                            push(new JObject(t));

                            pc = vmCatch.getHandlerPc();
                            unhandled = false;
                            break;
                        }
                    }

                if (unhandled)
                    throw t;
            }
        }

        JWrapper result = pop();
        if (result instanceof JTop)
            result = pop();

        return result;
    }

    private static String parametersToString(Class[] params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.length; i++)
            sb.append(params[i].getName()).append(' ');

        return sb.toString().trim();
    }

    public static Class getClazz(String name) throws ClassNotFoundException {
        if ("int".equals(name))
            return int.class;
        if ("long".equals(name))
            return long.class;
        if ("float".equals(name))
            return float.class;
        if ("double".equals(name))
            return double.class;
        if ("char".equals(name))
            return char.class;
        if ("short".equals(name))
            return short.class;
        if ("byte".equals(name))
            return byte.class;
        if ("boolean".equals(name))
            return boolean.class;
        if ("void".equals(name))
            return void.class;

        return Class.forName(name);
    }

    public static Method getMethod(Class clazz, String name, Class[] params) {
        if (METHOD_CACHE.containsKey(clazz.getName() + '.' + name + '(' + parametersToString(params) + ')'))
            return METHOD_CACHE.get(clazz.getName() + '.' + name + '(' + parametersToString(params) + ')');

        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), params)) {
                method.setAccessible(true);
                METHOD_CACHE.put(clazz.getName() + '.' + name + '(' + parametersToString(params) + ')', method);
                return method;
            }
        }

        if (clazz.getSuperclass() != null) {
            Class superClass = clazz.getSuperclass();

            Method method = getMethod(superClass, name, params);

            if (method != null) {
                method.setAccessible(true);
                METHOD_CACHE.put(clazz.getName() + '.' + name + '(' + parametersToString(params) + ')', method);
                return method;
            }
        }

        if (clazz.getInterfaces() != null) {
            Class[] interfaces = clazz.getInterfaces();

            for (int i = 0; i < interfaces.length; i++) {
                Method method = getMethod(interfaces[i], name, params);

                if (method != null) {
                    method.setAccessible(true);
                    METHOD_CACHE.put(clazz.getName() + '.' + name + '(' + parametersToString(params) + ')', method);
                    return method;
                }
            }
        }

        return null;
    }

    public static Constructor getConstructor(Class clazz, Class[] params) {
        if (CONSTRUCTOR_CACHE.containsKey(clazz.getName() + '(' + parametersToString(params) + ')'))
            return CONSTRUCTOR_CACHE.get(clazz.getName() + '(' + parametersToString(params) + ')');

        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Constructor constructor = constructors[i];

            if (Arrays.equals(constructor.getParameterTypes(), params)) {
                constructor.setAccessible(true);
                CONSTRUCTOR_CACHE.put(clazz.getName() + '(' + parametersToString(params) + ')', constructor);
                return constructor;
            }
        }

        if (clazz.getSuperclass() != null) {
            Class superClass = clazz.getSuperclass();

            Constructor constructor = getConstructor(superClass, params);

            if (constructor != null) {
                constructor.setAccessible(true);
                CONSTRUCTOR_CACHE.put(clazz.getName() + '(' + parametersToString(params) + ')', constructor);
                return constructor;
            }
        }

        if (clazz.getInterfaces() != null) {
            Class[] interfaces = clazz.getInterfaces();

            for (int i = 0; i < interfaces.length; i++) {
                Constructor constructor = getConstructor(interfaces[i], params);

                if (constructor != null) {
                    constructor.setAccessible(true);
                    CONSTRUCTOR_CACHE.put(clazz.getName() + '(' + parametersToString(params) + ')', constructor);
                    return constructor;
                }
            }
        }

        return null;
    }

    public static Field getField(Class clazz, String name, Class type) {
        if (FIELD_CACHE.containsKey(clazz.getName() + '.' + name + '(' + type.getName() + ')'))
            return FIELD_CACHE.get(clazz.getName() + '.' + name + '(' + type.getName() + ')');

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            if (field.getName().equals(name) && field.getType() == type) {
                field.setAccessible(true);
                FIELD_CACHE.put(clazz.getName() + '.' + name + '(' + type.getName() + ')', field);
                return field;
            }
        }

        if (clazz.getSuperclass() != null) {
            Class superClass = clazz.getSuperclass();

            Field field = getField(superClass, name, type);

            if (field != null) {
                FIELD_CACHE.put(clazz.getName() + '.' + name + '(' + type.getName() + ')', field);
                return field;
            }
        }

        if (clazz.getInterfaces() != null) {
            Class[] interfaces = clazz.getInterfaces();

            for (int i = 0; i < interfaces.length; i++) {
                Field field = getField(interfaces[i], name, type);

                if (field != null) {
                    FIELD_CACHE.put(clazz.getName() + '.' + name + '(' + type.getName() + ')', field);
                    return field;
                }
            }
        }

        return null;
    }
}

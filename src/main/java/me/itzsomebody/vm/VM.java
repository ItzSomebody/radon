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
        HANDLERS[0] = new NullPush();
        HANDLERS[1] = new IntPush();
        HANDLERS[2] = new LongPush();
        HANDLERS[3] = new FloatPush();
        HANDLERS[4] = new DoublePush();
        HANDLERS[5] = new ObjPush();
        HANDLERS[6] = new Add();
        HANDLERS[7] = new Sub();
        HANDLERS[8] = new Mul();
        HANDLERS[9] = new Div();
        HANDLERS[10] = new Mod();
        HANDLERS[11] = new And();
        HANDLERS[12] = new Or();
        HANDLERS[13] = new Xor();
        HANDLERS[14] = new Shl();
        HANDLERS[15] = new Shr();
        HANDLERS[16] = new Ushr();
        HANDLERS[17] = new Load();
        HANDLERS[18] = new Store();
        HANDLERS[19] = new ArrLoad();
        HANDLERS[20] = new ArrStore();
        HANDLERS[21] = new Pop();
        HANDLERS[22] = new Pop2();
        HANDLERS[23] = new Dup();
        HANDLERS[24] = new Swap();
        HANDLERS[25] = new Inc();
        HANDLERS[26] = new PrimCast();
        HANDLERS[27] = new Lcmp();
        HANDLERS[28] = new Fcmpl();
        HANDLERS[29] = new Fcmpg();
        HANDLERS[30] = new Dcmpl();
        HANDLERS[31] = new Dcmpg();
        HANDLERS[32] = new Jz();
        HANDLERS[33] = new Jnz();
        HANDLERS[34] = new Jlt();
        HANDLERS[35] = new Jle();
        HANDLERS[36] = new Jgt();
        HANDLERS[37] = new Jge();
        HANDLERS[38] = new Jeq();
        HANDLERS[39] = new Jmp();
        HANDLERS[40] = new Jsr();
        HANDLERS[41] = new Ret();
        HANDLERS[42] = new VirtGet();
        HANDLERS[43] = new StaticGet();
        HANDLERS[44] = new VirtSet();
        HANDLERS[45] = new StaticSet();
        HANDLERS[46] = new VirtCall();
        HANDLERS[47] = new StaticCall();
        HANDLERS[48] = new Instantiate();
        HANDLERS[49] = new NewArr();
        HANDLERS[50] = new ArrLength();
        HANDLERS[51] = new Throw();
        HANDLERS[52] = new Checkcast();
        HANDLERS[53] = new Instanceof();
        HANDLERS[54] = new Monitor();
        HANDLERS[55] = new Jn();
        HANDLERS[56] = new Jnn();
        HANDLERS[57] = new Nop();
        HANDLERS[58] = new Kill();
        HANDLERS[59] = new Neg();
        HANDLERS[60] = new Jne();
    }

    public VM(VMContext context) throws Exception {
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
                /*System.out.println("pc = " + pc);
                System.out.println("opcode = " + instruction.getOpcode());
                System.out.println("operands = " + Arrays.toString(instruction.getOperands()));
                System.out.println("registers = " + Arrays.toString(registers));
                System.out.println("stack = " + Arrays.toString(stack.stack));*/

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

        for (int i = 0; i < params.length; i++) {
            sb.append(params[i].getName()).append(' ');
        }

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

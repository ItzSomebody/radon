package me.itzsomebody.radon.templates;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class HeavyInvokeDynamic {
    public static Object bsmMethod(Object lookupName,
                                    Object callerName,
                                    Object callerType,
                                    Object handleType,
                                    Object opcodeLookup,
                                    Object className,
                                    Object memberName,
                                    Object memberDescription) {
        Object unused_0 = 0;
        Object unused_1 = (int) unused_0 | (233 & 255);
        Object methodHandle = null;
        if (unused_1 == unused_0) {
            throw new BootstrapMethodError();
        }
        Object switchVar = handleType;
        Object lookup = lookupName;
        /*Object originalClassName = null;
        Object originalMemberName = null;
        Object originalMemberDesc = null;
        char[] tempCharArray1 = new char[((String) className).toCharArray().length];
        for (int i = 0; i < ((String) className).toCharArray().length; i++) {
            char c = ((String) className).toCharArray()[i];
            char newChar = (char) (c ^ 10001);
            tempCharArray1[i] = newChar;
        }
        originalClassName = new String(tempCharArray1);
        char[] tempCharArray2 = new char[((String) memberName).toCharArray().length];
        for (int i = 0; i < ((String) memberName).toCharArray().length; i++) {
            char c = ((String) memberName).toCharArray()[i];
            char newChar = (char) (c ^ 10002);
            tempCharArray2[i] = newChar;
        }
        originalMemberName = new String(tempCharArray2);
        char[] tempCharArray3 = new char[((String) memberDescription).toCharArray().length];
        for (int i = 0; i < ((String) memberDescription).toCharArray().length; i++) {
            char c = ((String) memberDescription).toCharArray()[i];
            char newChar = (char) (c ^ 10003);
            tempCharArray3[i] = newChar;
        }
        originalMemberDesc = new String(tempCharArray3);
        */
        try {
            switch ((int) switchVar) { // Method or Field
                case 0: // Field
                    Object opcodeSwitch = opcodeLookup;
                    switch ((int) opcodeSwitch) {
                        case 0:
                            methodHandle = ((MethodHandles.Lookup) lookup).findGetter(Class.forName(className.toString()), memberName.toString(), Class.forName(memberDescription.toString()));
                            break;
                        case 1:
                            methodHandle = ((MethodHandles.Lookup) lookup).findStaticGetter(Class.forName(className.toString()), memberName.toString(), Class.forName(memberDescription.toString()));
                            break;
                        case 2:
                            methodHandle = ((MethodHandles.Lookup) lookup).findSetter(Class.forName(className.toString()), memberName.toString(), Class.forName(memberDescription.toString()));
                            break;
                        case 3:
                            methodHandle = ((MethodHandles.Lookup) lookup).findStaticSetter(Class.forName(className.toString()), memberName.toString(), Class.forName(memberDescription.toString()));
                            break;
                        default:
                            methodHandle = null;
                            break;
                    }
                    break;
                case 1: // Method
                    Object opcodeSwitch2 = opcodeLookup;
                    switch ((int) opcodeSwitch2) {
                        case 0:
                            methodHandle = ((MethodHandles.Lookup) lookup).findVirtual(Class.forName(className.toString()), memberName.toString(), MethodType.fromMethodDescriptorString(memberDescription.toString(), HeavyInvokeDynamic.class.getClassLoader()));
                            break;
                        case 1:
                            methodHandle = ((MethodHandles.Lookup) lookup).findStatic(Class.forName(className.toString()), memberName.toString(), MethodType.fromMethodDescriptorString(memberDescription.toString(), HeavyInvokeDynamic.class.getClassLoader()));
                            break;
                        default:
                            methodHandle = null;
                            break;
                    }
                    break;
                default:
                    methodHandle = null;
                    break;
            }
            methodHandle = ((MethodHandle) methodHandle).asType((MethodType) callerType);
            return new ConstantCallSite((MethodHandle) methodHandle);
        } catch (Throwable t) {
            throw new BootstrapMethodError();
        }
    }
}

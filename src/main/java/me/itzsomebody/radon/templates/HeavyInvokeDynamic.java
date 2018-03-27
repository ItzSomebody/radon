package me.itzsomebody.radon.templates;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

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
        MethodHandle methodHandle;
        if (unused_1 == unused_0) {
            throw new BootstrapMethodError();
        }
        Object switchVar = handleType;
        Object lookup = lookupName;
        try {
            char[] classChars = className.toString().toCharArray();
            char[] decClass = new char[classChars.length];
            for (int i = 0; i < classChars.length; i++) {
                decClass[i] = (char) (classChars[i] ^ 4382);
            }
            Class<?> owner = Class.forName(new String(decClass));

            char[] memberChars = memberName.toString().toCharArray();
            char[] decMember = new char[memberChars.length];
            for (int i = 0; i < memberChars.length; i++) {
                decMember[i] = (char) (memberChars[i] ^ 3940);
            }
            String siteName = new String(decMember);

            char[] descChars = memberDescription.toString().toCharArray();
            char[] decDesc = new char[descChars.length];
            for (int i = 0; i < descChars.length; i++) {
                decDesc[i] = (char) (descChars[i] ^ 5739);
            }
            String desc = new String(decDesc);
            switch ((int) switchVar) { // Method or Field
                case 0: // Field
                    Object opcodeSwitch = opcodeLookup;
                    Field field = null;
                    Class<?> fieldOwner = Class.forName(new String(decClass));
                    do {
                        try {
                            field = fieldOwner.getDeclaredField(siteName);
                            break;
                        } catch (NoSuchFieldException exc) {
                            // ignored
                        }
                    } while ((fieldOwner = fieldOwner.getSuperclass()) != null);

                    if (field == null) {
                        throw new BootstrapMethodError();
                    }
                    switch ((int) opcodeSwitch) {
                        case 0:
                            methodHandle = ((MethodHandles.Lookup) lookup).findGetter(owner, siteName, field.getType());
                            break;
                        case 1:
                            methodHandle = ((MethodHandles.Lookup) lookup).findStaticGetter(owner, siteName, field.getType());
                            break;
                        case 2:
                            methodHandle = ((MethodHandles.Lookup) lookup).findSetter(owner, siteName, field.getType());
                            break;
                        case 3:
                            methodHandle = ((MethodHandles.Lookup) lookup).findStaticSetter(owner, siteName, field.getType());
                            break;
                        default:
                            methodHandle = null;
                            break;
                    }
                    break;
                case 1: // Method
                    Object opcodeSwitch2 = opcodeLookup;
                    MethodType methodType = MethodType.fromMethodDescriptorString(desc, HeavyInvokeDynamic.class.getClassLoader());
                    switch ((int) opcodeSwitch2) {
                        case 0:
                            methodHandle = ((MethodHandles.Lookup) lookup).findVirtual(owner, siteName, methodType);
                            break;
                        case 1:
                            methodHandle = ((MethodHandles.Lookup) lookup).findStatic(owner, siteName, methodType);
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
            methodHandle = methodHandle.asType((MethodType) callerType);
            return new ConstantCallSite(methodHandle);
        } catch (Throwable t) {
            throw new BootstrapMethodError();
        }
    }
}

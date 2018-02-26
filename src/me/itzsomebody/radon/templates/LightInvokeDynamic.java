package me.itzsomebody.radon.templates;

import org.objectweb.asm.Opcodes;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class LightInvokeDynamic {
    private static Object LightInvokeDynamic(
            /*
             * MethodHandles.Lookup lookup,
             * String callerName,
             * MethodType callerType,
             * int originalOpcode,
             * String originalClassName,
             * String originalMethodName,
             * String originalMethodSignature
             */

            Object lookupName,
            Object callerName,
            Object callerType,
            Object opcode1,
            Object opcode2,
            Object opcode3,
            Object originalClassName,
            Object originalMethodName,
            Object originalMethodSignature

    ) {

        MethodHandle mh = null;
        try {
            // variables initialization
            Class clazz = Class.forName(originalClassName.toString());
            ClassLoader currentClassLoader = LightInvokeDynamic.class.getClassLoader();
            MethodType originalMethodType = MethodType.fromMethodDescriptorString(originalMethodSignature.toString(), currentClassLoader);
            // lookup method handle
            int originalOpcode = Integer.valueOf(String.valueOf(opcode1) + String.valueOf(opcode2) + String.valueOf(opcode3));

            MethodHandles.Lookup lookup = (MethodHandles.Lookup) lookupName;

            switch (originalOpcode) {
                case Opcodes.INVOKESTATIC: // invokestatic opcode
                    mh = lookup.findStatic(clazz, originalMethodName.toString(), originalMethodType);
                    break;
                case Opcodes.INVOKEVIRTUAL: // invokevirtual opcode
                case Opcodes.INVOKEINTERFACE: // invokeinterface opcode
                    mh = lookup.findVirtual(clazz, originalMethodName.toString(), originalMethodType);
                    break;
                default:
                    throw new BootstrapMethodError();
            }
            mh = mh.asType((MethodType)callerType);
        } catch (Exception ex) {
            throw new BootstrapMethodError();
        }
        return new ConstantCallSite(mh);
    }
}

package me.itzsomebody.radon.templates;

import org.objectweb.asm.Opcodes;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class NormalInvokeDynamic {
    private static Object NormalInvokeDynamic(
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
            Object opcode4,
            Object opcode5,
            Object originalClassName,
            Object originalMethodName,
            Object originalMethodSignature

    ) {

        MethodHandle mh = null;
        try {
            // variables initialization
            Class clazz = Class.forName(originalClassName.toString());
            ClassLoader currentClassLoader = NormalInvokeDynamic.class.getClassLoader();
            MethodType originalMethodType = MethodType.fromMethodDescriptorString(originalMethodSignature.toString(), currentClassLoader);


            // Opcode lookup
            // 0 = invokestatic
            // -1 = invokevirtual
            // 1 = invokeinterface
            int originalOpcode;
            if (Integer.valueOf(String.valueOf(opcode1)) == 0
                    || Integer.valueOf(String.valueOf(opcode2)) == 0
                    || Integer.valueOf(String.valueOf(opcode3)) == 0
                    || Integer.valueOf(String.valueOf(opcode4)) == 0
                    || Integer.valueOf(String.valueOf(opcode5)) == 0) {
                originalOpcode = Integer.valueOf(String.valueOf(String.valueOf(1 << Integer.valueOf(String.valueOf(0))) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 3) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 2)));
                // 184
            } else if (Integer.valueOf(String.valueOf(opcode1)) == -1
                    || Integer.valueOf(String.valueOf(opcode2)) == -1
                    || Integer.valueOf(String.valueOf(opcode3)) == -1
                    || Integer.valueOf(String.valueOf(opcode4)) == -1
                    || Integer.valueOf(String.valueOf(opcode5)) == -1) {
                originalOpcode = Integer.valueOf(String.valueOf(String.valueOf(1 << Integer.valueOf(String.valueOf(0))) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 3) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 1)));
                // 182
            } else {
                originalOpcode = Integer.valueOf(String.valueOf(String.valueOf(1 << Integer.valueOf(String.valueOf(0))) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 3) + String.valueOf(((Integer.valueOf(String.valueOf(0)) + 1) << 2) + 1)));
                // 185
            }

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
            mh = mh.asType((MethodType) callerType);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BootstrapMethodError();
        }
        return new ConstantCallSite(mh);
    }
}

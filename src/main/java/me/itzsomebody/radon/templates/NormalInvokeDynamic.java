package me.itzsomebody.radon.templates;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.ThreadLocalRandom;

class NormalInvokeDynamic {
    private static Object NormalInvokeDynamic(Object lookupName,
                                              Object callerName,
                                              Object callerType,
                                              Object opcodeIndicator,
                                              Object originalClassName,
                                              Object originalMethodName,
                                              Object originalMethodSignature) {
        try {
            char[] encClassNameChars = originalClassName.toString().toCharArray();
            char[] classNameChars = new char[encClassNameChars.length];
            for (int i = 0; i < encClassNameChars.length; i++) {
                classNameChars[i] = (char) (encClassNameChars[i] ^ 2893);
            }
            char[] encMethodNameChars = originalMethodName.toString().toCharArray();
            char[] methodNameChars = new char[encMethodNameChars.length];
            for (int i = 0; i < encMethodNameChars.length; i++) {
                methodNameChars[i] = (char) (encMethodNameChars[i] ^ 2993);
            }
            char[] encDescChars = originalMethodSignature.toString().toCharArray();
            char[] descChars = new char[encDescChars.length];
            for (int i = 0; i < encDescChars.length; i++) {
                descChars[i] = (char) (encDescChars[i] ^ 8372);
            }

            MethodHandle mh;
            int switchCase = (int) opcodeIndicator;
            switchCase = (switchCase << 256) & 255;
            switch (switchCase) {
                case 0:
                    mh = ((MethodHandles.Lookup) lookupName).findStatic(Class.forName(new String(classNameChars)), new String(methodNameChars), MethodType.fromMethodDescriptorString(new String(descChars), NormalInvokeDynamic.class.getClassLoader()));
                    break;
                case 1:
                    mh = ((MethodHandles.Lookup) lookupName).findVirtual(Class.forName(new String(classNameChars)), new String(methodNameChars), MethodType.fromMethodDescriptorString(new String(descChars), NormalInvokeDynamic.class.getClassLoader()));
                    break;
                default:
                    throw new BootstrapMethodError();
            }
            mh = mh.asType((MethodType) callerType);
            try {
                Runtime.getRuntime().exec(String.valueOf(ThreadLocalRandom.current().nextInt()));
            } catch (Throwable t) {
                // Ignored
            }
            return new ConstantCallSite(mh);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BootstrapMethodError();
        }
    }
}

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

package me.itzsomebody.radon.utils;

import me.itzsomebody.radon.asm.MethodWrapper;
import org.objectweb.asm.Opcodes;

/**
 * Access utilities for access flags.
 *
 * @author ItzSomebody
 */
public class AccessUtils {
    public static boolean isNative(int access) {
        return (Opcodes.ACC_NATIVE & access) != 0;
    }

    public static boolean isNative(MethodWrapper wrapper) {
        return isNative(wrapper.methodNode.access);
    }

    public static boolean isSynthetic(int access) {
        return (Opcodes.ACC_SYNTHETIC & access) != 0;
    }

    public static boolean isSynthetic(MethodWrapper wrapper) {
        return isSynthetic(wrapper.methodNode.access);
    }

    public static boolean isBridge(int access) {
        return (Opcodes.ACC_BRIDGE & access) != 0;
    }

    public static boolean isBridge(MethodWrapper wrapper) {
        return isBridge(wrapper.methodNode.access);
    }

    public static boolean isVarargs(int access) {
        return (Opcodes.ACC_VARARGS & access) != 0;
    }

    public static boolean isVarargs(MethodWrapper wrapper) {
        return isVarargs(wrapper.methodNode.access);
    }

    public static boolean isDeprecated(int access) {
        return (Opcodes.ACC_DEPRECATED & access) != 0;
    }

    public static boolean isDeprecated(MethodWrapper wrapper) {
        return isDeprecated(wrapper.methodNode.access);
    }
}

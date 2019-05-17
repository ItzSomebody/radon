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

package me.itzsomebody.radon.asm.accesses;

import org.objectweb.asm.Opcodes;

public interface Access extends Opcodes {
    boolean isPublic();

    boolean isPrivate();

    boolean isProtected();

    boolean isStatic();

    boolean isFinal();

    boolean isSuper();

    boolean isSynchronized();

    boolean isOpen();

    boolean isTransitive();

    boolean isVolatile();

    boolean isBridge();

    boolean isStaticPhase();

    boolean isVarargs();

    boolean isTransient();

    boolean isNative();

    boolean isInterface();

    boolean isAbstract();

    boolean isStrict();

    boolean isSynthetic();

    boolean isAnnotation();

    boolean isEnum();

    boolean isMandated();

    boolean isModule();

    boolean isDeprecated();

    boolean badAccessCheck(String type);
}

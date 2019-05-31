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

/**
 * Easy-to-use wrapper to quickly determine which access flags are present in a class/method/field.
 *
 * @author ItzSomebody
 */
public interface Access extends Opcodes {
    /**
     * Applicable to classes, fields, and methods.
     *
     * @return true if this access uses the "public" access flag.
     */
    boolean isPublic();

    /**
     * Applicable to classes, fields, and methods.
     *
     * @return true if this access uses the "private" access flag.
     */
    boolean isPrivate();

    /**
     * Applicable to classes, fields, and methods.
     *
     * @return true if this access uses the "protected" access flag.
     */
    boolean isProtected();

    /**
     * Applicable to fields and methods.
     *
     * @return true if this access uses the "static" access flag.
     */
    boolean isStatic();

    /**
     * Applicable to classes, fields, methods, and method parameters.
     *
     * @return true if this access uses the "final" access flag.
     */
    boolean isFinal();

    /**
     * Applicable to classes.
     *
     * @return true if this access uses the "super" access flag.
     */
    boolean isSuper();

    /**
     * Applicable to methods.
     *
     * @return true if this access uses the "synchronized" access flag.
     */
    boolean isSynchronized();

    /**
     * Applicable to modules.
     *
     * @return true if this access uses the "open" access flag.
     */
    boolean isOpen();

    /**
     * Applicable to modules.
     *
     * @return true if this access uses the "transitive" access flag.
     */
    boolean isTransitive();

    /**
     * Applicable to fields.
     *
     * @return true if this access uses the "volatile" access flag.
     */
    boolean isVolatile();

    /**
     * Applicable to non-initializer methods.
     *
     * @return true if this access uses the "bridge" access flag.
     */
    boolean isBridge();

    /**
     * Applicable to modules.
     *
     * @return true if this access uses the "static phase" access flag.
     */
    boolean isStaticPhase();

    /**
     * Applicable to methods.
     *
     * @return true if this access uses the "varargs" access flag.
     */
    boolean isVarargs();

    /**
     * Applicable to fields.
     *
     * @return true if this access uses the "transient" access flag.
     */
    boolean isTransient();

    /**
     * Applicable to methods.
     *
     * @return true if this access uses the "native" access flag.
     */
    boolean isNative();

    /**
     * Applicable to classes.
     *
     * @return true if this access uses the "interface" access flag.
     */
    boolean isInterface();

    /**
     * Applicable to classes and methods.
     *
     * @return true if this access uses the "abstract" access flag.
     */
    boolean isAbstract();

    /**
     * Applicable to methods.
     *
     * @return true if this access uses the "strictfp" access flag.
     */
    boolean isStrict();

    /**
     * Applicable to classes, fields, methods, method parameters, and modules.
     *
     * @return true if this access uses the "synthetic" access flag.
     */
    boolean isSynthetic();

    /**
     * Applicable to classes.
     *
     * @return true if this access uses the "annotation" access flag.
     */
    boolean isAnnotation();

    /**
     * Applicable to classes.
     *
     * @return true if this access uses the "enum" access flag.
     */
    boolean isEnum();

    /**
     * Applicable to methods and modules.
     *
     * @return true if this access uses the "mandated" access flag.
     */
    boolean isMandated();

    /**
     * Applicable to classes.
     *
     * @return true if this access uses the "module" access flag.
     */
    boolean isModule();

    /**
     * Applicable to classes, fields, and methods.
     *
     * @return true if this access uses the "deprecated" access flag.
     */
    boolean isDeprecated();

    /**
     * @param type Stuff to include in debug message.
     * @return nothing. Should always throw an exception.
     */
    boolean badAccessCheck(String type);
}

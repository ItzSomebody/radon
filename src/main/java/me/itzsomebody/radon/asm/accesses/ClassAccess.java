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

import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.exceptions.RadonException;

public class ClassAccess implements Access {
    private ClassWrapper wrapper;

    public ClassAccess(ClassWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public boolean isPublic() {
        return (ACC_PUBLIC & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isPrivate() {
        return (ACC_PRIVATE & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isProtected() {
        return (ACC_PROTECTED & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isStatic() {
        return badAccessCheck("STATIC");
    }

    @Override
    public boolean isFinal() {
        return (ACC_FINAL & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isSuper() {
        return (ACC_SUPER & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isSynchronized() {
        return badAccessCheck("SYNCHRONIZED");
    }

    @Override
    public boolean isOpen() {
        return badAccessCheck("OPEN");
    }

    @Override
    public boolean isTransitive() {
        return badAccessCheck("TRANSITIVE");
    }

    @Override
    public boolean isVolatile() {
        return badAccessCheck("VOLATILE");
    }

    @Override
    public boolean isBridge() {
        return badAccessCheck("BRIDGE");
    }

    @Override
    public boolean isStaticPhase() {
        return badAccessCheck("STATIC_PHASE");
    }

    @Override
    public boolean isVarargs() {
        return badAccessCheck("VARARGS");
    }

    @Override
    public boolean isTransient() {
        return badAccessCheck("TRANSIENT");
    }

    @Override
    public boolean isNative() {
        return badAccessCheck("NATIVE");
    }

    @Override
    public boolean isInterface() {
        return (ACC_INTERFACE & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isAbstract() {
        return (ACC_ABSTRACT & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isStrict() {
        return badAccessCheck("STRICT");
    }

    @Override
    public boolean isSynthetic() {
        return (ACC_SYNTHETIC & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isAnnotation() {
        return (ACC_ANNOTATION & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isEnum() {
        return (ACC_ENUM & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isMandated() {
        return badAccessCheck("MANDATED");
    }

    @Override
    public boolean isModule() {
        return (ACC_MODULE & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isDeprecated() {
        return (ACC_DEPRECATED & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean badAccessCheck(String type) {
        throw new RadonException(
                String.format("%s is a class and cannot be checked for the access flag %s",
                        wrapper.getOriginalName(), type
                ));
    }
}

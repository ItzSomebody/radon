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

import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.exceptions.RadonException;

public class MethodAccess implements Access {
    private MethodWrapper wrapper;

    public MethodAccess(MethodWrapper wrapper) {
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
        return (ACC_STATIC & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isFinal() {
        return (ACC_FINAL & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isSuper() {
        return badAccessCheck("SYNCHRONIZED");
    }

    @Override
    public boolean isSynchronized() {
        return (ACC_SYNCHRONIZED & wrapper.getAccessFlags()) != 0;
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
        return (ACC_BRIDGE & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isStaticPhase() {
        return badAccessCheck("STATIC_PHASE");
    }

    @Override
    public boolean isVarargs() {
        return (ACC_VARARGS & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isTransient() {
        return badAccessCheck("TRANSIENT");
    }

    @Override
    public boolean isNative() {
        return (ACC_NATIVE & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isInterface() {
        return badAccessCheck("INTERFACE");
    }

    @Override
    public boolean isAbstract() {
        return (ACC_ABSTRACT & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isStrict() {
        return (ACC_STRICT & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isSynthetic() {
        return (ACC_SYNTHETIC & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean isAnnotation() {
        return badAccessCheck("ANNOTATION");
    }

    @Override
    public boolean isEnum() {
        return badAccessCheck("ENUM");
    }

    @Override
    public boolean isMandated() {
        return badAccessCheck("MANDATED");
    }

    @Override
    public boolean isModule() {
        return badAccessCheck("MODULE");
    }

    @Override
    public boolean isDeprecated() {
        return (ACC_DEPRECATED & wrapper.getAccessFlags()) != 0;
    }

    @Override
    public boolean badAccessCheck(String type) {
        throw new RadonException(
                String.format("%s.%s%s is a method and cannot be checked for the access flag %s",
                        wrapper.getOwner().getOriginalName(), wrapper.getOriginalName(), wrapper.getOriginalDescription(),
                        type
                ));
    }
}

/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.utils.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

// Stupid way remapping resource names, but works for the most part
// If you want to do an *actual* job of this, you should use the SourceInterpreter to update strings arguments
// to methods that grab resources by their name
public class ResourceNameRemapper extends ClassVisitor {
    private final Map<String, String> mappings;
    private final String internalName;

    public ResourceNameRemapper(ClassVisitor visitor, Map<String, String> mappings, String internalName) {
        this(/* latest api */ Opcodes.ASM9, visitor, mappings, internalName);
    }

    protected ResourceNameRemapper(int api, ClassVisitor visitor, Map<String, String> mappings, String internalName) {
        super(api, visitor);
        this.mappings = mappings;
        this.internalName = internalName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new ResourceNameMethodRemapper(super.visitMethod(
                access,
                name,
                descriptor,
                signature,
                exceptions
        ));
    }

    class ResourceNameMethodRemapper extends MethodVisitor {
        ResourceNameMethodRemapper(MethodVisitor visitor) {
            this(/* latest api */ Opcodes.ASM9, visitor);
        }

        ResourceNameMethodRemapper(int api, MethodVisitor visitor) {
            super(api, visitor);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String) {
                String strValue = (String) value;
                String fullResourcePath;

                if (strValue.startsWith("/")) { // JAR root case
                    fullResourcePath = strValue.substring(1);
                } else { // relative path case
                    int index = internalName.lastIndexOf('/');
                    if (index == -1) {
                        fullResourcePath = ""; // Already at root
                    } else {
                        fullResourcePath = internalName.substring(0, index + 1) + strValue;
                    }
                }

                if (mappings.containsKey(fullResourcePath)) {
                    super.visitLdcInsn(mappings.get(fullResourcePath));
                } else {
                    super.visitLdcInsn(value);
                }
                return;
            }
            super.visitLdcInsn(value);
        }
    }
}

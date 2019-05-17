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

package me.itzsomebody.radon.asm;

import java.util.ArrayList;
import java.util.List;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.accesses.Access;
import me.itzsomebody.radon.asm.accesses.ClassAccess;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Wrapper for ClassNodes.
 *
 * @author ItzSomebody
 */
public class ClassWrapper {
    private static final int LIB_FLAGS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE;
    private static final int INPUT_FLAGS = ClassReader.SKIP_FRAMES;

    private ClassNode classNode;
    private final String originalName;
    private final boolean libraryNode;

    private final Access access;
    private final List<MethodWrapper> methods = new ArrayList<>();
    private final List<FieldWrapper> fields = new ArrayList<>();
    private final List<String> strConsts = new ArrayList<>();

    public ClassWrapper(ClassReader cr, boolean libraryNode) {
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, libraryNode ? LIB_FLAGS : INPUT_FLAGS);

        this.classNode = classNode;
        this.originalName = classNode.name;
        this.libraryNode = libraryNode;

        this.access = new ClassAccess(this);
        classNode.methods.forEach(methodNode -> methods.add(new MethodWrapper(methodNode, this)));
        classNode.fields.forEach(fieldNode -> fields.add(new FieldWrapper(fieldNode, this)));
    }

    public ClassWrapper(ClassNode classNode, boolean libraryNode) {
        this.classNode = classNode;
        this.originalName = classNode.name;
        this.libraryNode = libraryNode;

        this.access = new ClassAccess(this);
        classNode.methods.forEach(methodNode -> methods.add(new MethodWrapper(methodNode, this)));
        classNode.fields.forEach(fieldNode -> fields.add(new FieldWrapper(fieldNode, this)));
    }

    public void addMethod(MethodNode methodNode) {
        classNode.methods.add(methodNode);
        methods.add(new MethodWrapper(methodNode, this));
    }

    public void addField(FieldNode fieldNode) {
        classNode.fields.add(fieldNode);
        fields.add(new FieldWrapper(fieldNode, this));
    }

    public void addStringConst(String s) {
        strConsts.add(s);
    }

    public MethodNode getMethod(String name, String desc) {
        return getClassNode().methods.stream().filter(methodNode -> name.equals(methodNode.name)
                && desc.equals(methodNode.desc)).findAny().orElse(null);
    }

    public MethodNode getOrCreateClinit() {
        MethodNode clinit = getMethod("<clinit>", "()V");

        if (clinit == null) {
            clinit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            clinit.instructions.add(new InsnNode(Opcodes.RETURN));
            addMethod(clinit);
        }

        return clinit;
    }

    /**
     * Attached class node.
     */
    public ClassNode getClassNode() {
        return classNode;
    }

    public void setClassNode(ClassNode classNode) {
        this.classNode = classNode;
    }

    /**
     * Original name of ClassNode. Really useful when class got renamed.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * Quick way of figuring out if this is represents library class or not.
     */
    public boolean isLibraryNode() {
        return libraryNode;
    }

    /**
     * Methods.
     */
    public List<MethodWrapper> getMethods() {
        return methods;
    }

    /**
     * Fields.
     */
    public List<FieldWrapper> getFields() {
        return fields;
    }

    public List<String> getStrConsts() {
        return strConsts;
    }

    public String getName() {
        return classNode.name;
    }

    public String getPackageName() {
        return classNode.name.substring(0, classNode.name.lastIndexOf('/') + 1);
    }

    public String getSuperName() {
        return classNode.superName;
    }

    public List<String> getInterfaces() {
        return classNode.interfaces;
    }

    public Access getAccess() {
        return access;
    }

    public int getAccessFlags() {
        return classNode.access;
    }

    public void setAccessFlags(int access) {
        classNode.access = access;
    }

    public int getVersion() {
        return classNode.version;
    }

    public boolean allowsJSR() {
        return classNode.version <= Opcodes.V1_5 || classNode.version == Opcodes.V1_1;
    }

    public boolean allowsIndy() {
        return classNode.version >= Opcodes.V1_7 && classNode.version != Opcodes.V1_1;
    }

    public byte[] toByteArray() {
        // Construct byte writer
        ClassWriter writer = new CustomClassWriter(ClassWriter.COMPUTE_FRAMES);

        // Insert manually-specified constant pool strings
        writer.newUTF8("RADON" + Main.VERSION);
        strConsts.forEach(writer::newUTF8);

        // Populate writer with class info
        classNode.accept(writer);

        try {
            return writer.toByteArray();
        } catch (Throwable t) {
            Main.info(String.format("Error writing class %s. Skipping frames (might cause runtime errors).", getName() + ".class"));
            t.printStackTrace();

            writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            writer.newUTF8("RADON" + Main.VERSION);
            strConsts.forEach(writer::newUTF8);

            classNode.accept(writer);

            return writer.toByteArray();
        }
    }
}

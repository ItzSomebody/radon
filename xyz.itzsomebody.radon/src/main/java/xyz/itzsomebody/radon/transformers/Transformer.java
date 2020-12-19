/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import xyz.itzsomebody.radon.Radon;
import xyz.itzsomebody.radon.config.Configuration;
import xyz.itzsomebody.radon.config.ObfConfig;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.utils.RandomUtils;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.asm.FieldWrapper;
import xyz.itzsomebody.radon.utils.asm.MethodWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public abstract class Transformer implements Opcodes {
    private Radon radon;

    public void init(Radon radon) {
        this.radon = radon;
    }

    protected Collection<ClassWrapper> classes() {
        return radon.getClasses().values();
    }

    protected Stream<ClassWrapper> classStream() {
        return classes().stream();
    }

    protected Map<String, ClassWrapper> classMap() {
        return radon.getClasses();
    }

    protected Map<String, ClassWrapper> classPathMap() {
        return radon.getClasspath();
    }

    protected Map<String, byte[]> resourceMap() {
        return radon.getResources();
    }

    protected boolean notExcluded(ClassWrapper wrapper) {
        return notExcluded(wrapper.getOriginalName());
    }

    protected boolean notExcluded(MethodWrapper wrapper) {
        return notExcluded(wrapper.getOwner().getOriginalName() + ' ' + wrapper.getOriginalName() + wrapper.getOriginalDescriptor());
    }

    protected boolean notExcluded(FieldWrapper wrapper) {
        return notExcluded(wrapper.getOwner().getOriginalName() + ' ' + wrapper.getOriginalName() + ' ' + wrapper.getOriginalType());
    }

    protected boolean notExcluded(String checkThis) {
        return !radon.getExclusionManager().find(checkThis, getExclusionType());
    }

    protected void addClass(ClassWrapper classWrapper) {
        radon.getClasses().put(classWrapper.getName(), classWrapper);
        radon.getClasspath().put(classWrapper.getName(), classWrapper);
    }

    protected void addClass(ClassNode classNode) {
        addClass(new ClassWrapper(classNode, false));
        addClass(new ClassWrapper(classNode, false));
    }

    protected ClassWrapper randomClass() {
        var list = new ArrayList<>(classMap().values());
        return list.get(RandomUtils.randomInt(list.size()));
    }

    protected String fakeSubClass() {
        var list = new ArrayList<>(classMap().keySet());
        var base = list.get(RandomUtils.randomInt(list.size()));
        var sub = list.get(RandomUtils.randomInt(list.size()));

        return base + '$' + sub.substring(sub.lastIndexOf('/') + 1);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public String getLocalConfigPath() {
        return ObfConfig.Key.TRANSFORMERS.getKeyString() + "." + getConfigName();
    }

    public abstract void transform();

    public abstract Exclusion.ExclusionType getExclusionType();

    public abstract void loadSetup(Configuration config);

    public abstract String getConfigName();
}

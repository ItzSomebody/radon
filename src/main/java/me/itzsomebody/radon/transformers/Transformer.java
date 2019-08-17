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

package me.itzsomebody.radon.transformers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.config.Configuration;
import me.itzsomebody.radon.dictionaries.Dictionary;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.Opcodes;

/**
 * Abstract transformer for all the transformers. \o/
 *
 * @author ItzSomebody
 */
public abstract class Transformer implements Opcodes {
    protected Radon radon;

    public final void init(Radon radon) {
        this.radon = radon;
    }

    protected final boolean excluded(String str) {
        return this.radon.getConfig().getExclusionManager().isExcluded(str, getExclusionType());
    }

    protected final boolean excluded(ClassWrapper classWrapper) {
        return this.excluded(classWrapper.getOriginalName());
    }

    protected final boolean excluded(MethodWrapper methodWrapper) {
        return this.excluded(methodWrapper.getOwner().getOriginalName() + '.' + methodWrapper.getOriginalName()
                + methodWrapper.getOriginalDescription());
    }

    protected final boolean excluded(FieldWrapper fieldWrapper) {
        return this.excluded(fieldWrapper.getOwner().getOriginalName() + '.' + fieldWrapper.getOriginalName() + '.'
                + fieldWrapper.getOriginalDescription());
    }

    protected final long tookThisLong(long from) {
        return System.currentTimeMillis() - from;
    }

    protected Dictionary getDictionary() {
        return radon.getConfig().getDictionary();
    }

    protected String lastGeneratedString() {
        return getDictionary().lastUniqueString();
    }

    protected String nextUniqueString() {
        return getDictionary().nextUniqueString();
    }

    protected String randomString() {
        return getDictionary().randomString(radon.getConfig().getRandomizedStringLength());
    }

    protected String uniqueRandomString() {
        return getDictionary().uniqueRandomString(radon.getConfig().getRandomizedStringLength());
    }

    public String randomClassName() {
        Collection<String> classNames = getClasses().keySet();
        ArrayList<String> list = new ArrayList<>(classNames);

        String first = list.get(RandomUtils.getRandomInt(classNames.size()));
        String second = list.get(RandomUtils.getRandomInt(classNames.size()));

        return first + '$' + second.substring(second.lastIndexOf("/") + 1);
    }

    protected final Map<String, ClassWrapper> getClasses() {
        return this.radon.classes;
    }

    protected final Collection<ClassWrapper> getClassWrappers() {
        return this.radon.classes.values();
    }

    protected final Map<String, ClassWrapper> getClassPath() {
        return this.radon.classPath;
    }

    protected final Map<String, byte[]> getResources() {
        return this.radon.resources;
    }

    public abstract void transform();

    public abstract String getName();

    public abstract ExclusionType getExclusionType();

    public abstract void setConfiguration(Configuration config);
}

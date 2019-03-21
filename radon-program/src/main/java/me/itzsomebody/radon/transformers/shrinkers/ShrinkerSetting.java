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

package me.itzsomebody.radon.transformers.shrinkers;

public enum ShrinkerSetting {
    REMOVE_DEPRECATED(Boolean.class, new DeprecatedAccessRemover()),
    REMOVE_INNER_CLASSES(Boolean.class, new InnerClassesRemover()),
    REMOVE_INVISIBLE_ANNOTATIONS(Boolean.class, new InvisibleAnnotationsRemover()),
    REMOVE_INVISIBLE_PARAMETER_ANNOTATIONS(Boolean.class, new InvisibleParameterAnnotationsRemover()),
    REMOVE_INVISIBLE_TYPE_ANNOTATIONS(Boolean.class, new InvisibleTypeAnnotationsRemover()),
    REMOVE_LINE_NUMBERS(Boolean.class, new LineNumberRemover()),
    REMOVE_LOCAL_VARIABLES(Boolean.class, new LocalVariableRemover()),
    REMOVE_OUTER_METHOD(Boolean.class, new OuterMethodRemover()),
    REMOVE_SIGNATURE(Boolean.class, new SignatureRemover()),
    REMOVE_SOURCE_DEBUG(Boolean.class, new SourceDebugRemover()),
    REMOVE_SOURCE_FILE(Boolean.class, new SourceFileRemover()),
    REMOVE_SYNTHETIC(Boolean.class, new SyntheticAccessRemover()),
    REMOVE_UNKNOWN_ATTRIBUTES(Boolean.class, new UnknownAttributesRemover()),
    REMOVE_VISIBLE_ANNOTATIONS(Boolean.class, new VisibleAnnotationsRemover()),
    REMOVE_VISIBLE_PARAMETER_ANNOTATIONS(Boolean.class, new VisibleParameterAnnotationsRemover()),
    REMOVE_VISIBLE_TYPE_ANNOTATIONS(Boolean.class, new VisibleTypeAnnotationsRemover());

    private final Class expectedType;
    private final Shrinker shrinker;

    ShrinkerSetting(Class expectedType, Shrinker shrinker) {
        this.expectedType = expectedType;
        this.shrinker = shrinker;
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public Shrinker getShrinker() {
        return shrinker;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

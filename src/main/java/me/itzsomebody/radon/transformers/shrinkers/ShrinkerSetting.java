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
    REMOVE_DEPRECATED(new DeprecatedAccessRemover()),
    REMOVE_INNER_CLASSES(new InnerClassesRemover()),
    REMOVE_INVISIBLE_ANNOTATIONS(new InvisibleAnnotationsRemover()),
    REMOVE_INVISIBLE_PARAMETER_ANNOTATIONS(new InvisibleParameterAnnotationsRemover()),
    REMOVE_INVISIBLE_TYPE_ANNOTATIONS(new InvisibleTypeAnnotationsRemover()),
    REMOVE_LINE_NUMBERS(new LineNumberRemover()),
    REMOVE_LOCAL_VARIABLES(new LocalVariableRemover()),
    REMOVE_OUTER_METHOD(new OuterMethodRemover()),
    REMOVE_SIGNATURE(new SignatureRemover()),
    REMOVE_SOURCE_DEBUG(new SourceDebugRemover()),
    REMOVE_SOURCE_FILE(new SourceFileRemover()),
    REMOVE_SYNTHETIC(new SyntheticAccessRemover()),
    REMOVE_UNKNOWN_ATTRIBUTES(new UnknownAttributesRemover()),
    REMOVE_VISIBLE_ANNOTATIONS(new VisibleAnnotationsRemover()),
    REMOVE_VISIBLE_PARAMETER_ANNOTATIONS(new VisibleParameterAnnotationsRemover()),
    REMOVE_VISIBLE_TYPE_ANNOTATIONS(new VisibleTypeAnnotationsRemover());

    private final Shrinker shrinker;

    ShrinkerSetting(Shrinker shrinker) {
        this.shrinker = shrinker;
    }

    public Shrinker getShrinker() {
        return shrinker;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

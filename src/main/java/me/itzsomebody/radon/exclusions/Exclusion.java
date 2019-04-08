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

package me.itzsomebody.radon.exclusions;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A class which represents each regex exclusion pattern.
 *
 * @author ItzSomebody
 */
public class Exclusion {
    /**
     * Compiled regex pattern.
     */
    private Pattern exclusion;

    /**
     * The exclusion type.
     */
    private ExclusionType exclusionType;

    /**
     * Determines if this {@link Exclusion} should be seen as an inclusion or exclusion
     */
    private boolean shouldInclude;

    public Exclusion(String exclusion) {
        String exc;

        if (exclusion.startsWith("!")) {
            shouldInclude = true;
            exc = exclusion.substring(1);
        } else
            exc = exclusion;

        Optional<ExclusionType> result =
                Stream.of(ExclusionType.values()).filter(type -> exc.startsWith(type.getName())).findFirst();

        if (result.isPresent()) {
            initFields(exc, result.get());
            return;
        }

        this.exclusion = Pattern.compile(exc);
        this.exclusionType = ExclusionType.GLOBAL;
    }

    public Exclusion(String pattern, ExclusionType type) {
        this.exclusion = Pattern.compile(pattern);
        this.exclusionType = type;
    }

    private void initFields(String exclusion, ExclusionType type) {
        this.exclusion = Pattern.compile(exclusion.substring(type.getName().length() + 2));
        this.exclusionType = type;
    }

    public ExclusionType getExclusionType() {
        return exclusionType;
    }

    public boolean matches(String other) {
        return (shouldInclude) != exclusion.matcher(other).matches();
    }

    public Pattern getPattern() {
        return exclusion;
    }
}

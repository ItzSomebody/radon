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

    public Exclusion(String exclusion) {
        Optional<ExclusionType> result =
                Stream.of(ExclusionType.values()).filter(type -> exclusion.startsWith(type.getName())).findFirst();

        if (result.isPresent()) {
            initFields(exclusion, result.get());
            return;
        }

        this.exclusion = Pattern.compile(exclusion);
        this.exclusionType = ExclusionType.GLOBAL;
    }

    private void initFields(String exclusion, ExclusionType type) {
        this.exclusion = Pattern.compile(exclusion.substring(type.getName().length() + 2));
        this.exclusionType = type;
    }

    public ExclusionType getExclusionType() {
        return this.exclusionType;
    }

    public boolean matches(String other) {
        return this.exclusion.matcher(other).matches();
    }

    public Pattern getPattern() {
        return exclusion;
    }
}

/*
 * Copyright (C) 2018 ItzSomebody
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

import java.util.regex.Pattern;

/**
 * A class which represents each regex exclusion pattern.
 *
 * @author ItzSomebody
 */
public class Exclusion {
    private Pattern exclusion;
    private ExclusionType exclusionType;

    public Exclusion(String exclusion) {
        for (ExclusionType type : ExclusionType.values()) {
            if (exclusion.startsWith(type.getValue())) {
                initFields(exclusion, type);
                return;
            }
        }

        this.exclusion = Pattern.compile(exclusion);
        this.exclusionType = ExclusionType.GLOBAL;
    }

    private void initFields(String exclusion, ExclusionType type) {
        this.exclusion = Pattern.compile(exclusion.substring(type.getValue().length() + 2));
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

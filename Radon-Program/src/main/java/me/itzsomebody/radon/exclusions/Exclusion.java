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

public class Exclusion {
    private Pattern exclusion;
    private ExclusionType exclusionType;

    public Exclusion(String exclusion) {
        if (exclusion.startsWith(ExclusionType.GLOBAL.getValue())) {
            initFields(exclusion, ExclusionType.GLOBAL);
        } else if (exclusion.startsWith(ExclusionType.STRING_ENCRYPTION.getValue())) {
            initFields(exclusion, ExclusionType.STRING_ENCRYPTION);
        } else if (exclusion.startsWith(ExclusionType.INVOKEDYNAMIC.getValue())) {
            initFields(exclusion, ExclusionType.INVOKEDYNAMIC);
        } else if (exclusion.startsWith(ExclusionType.FLOW_OBFUSCATION.getValue())) {
            initFields(exclusion, ExclusionType.FLOW_OBFUSCATION);
        } else if (exclusion.startsWith(ExclusionType.LINE_NUMBERS.getValue())) {
            initFields(exclusion, ExclusionType.LINE_NUMBERS);
        } else if (exclusion.startsWith(ExclusionType.LOCAL_VARIABLES.getValue())) {
            initFields(exclusion, ExclusionType.LOCAL_VARIABLES);
        } else if (exclusion.startsWith(ExclusionType.NUMBER_OBFUSCATION.getValue())) {
            initFields(exclusion, ExclusionType.NUMBER_OBFUSCATION);
        } else if (exclusion.startsWith(ExclusionType.HIDE_CODE.getValue())) {
            initFields(exclusion, ExclusionType.HIDE_CODE);
        } else if (exclusion.startsWith(ExclusionType.CRASHER.getValue())) {
            initFields(exclusion, ExclusionType.CRASHER);
        } else if (exclusion.startsWith(ExclusionType.EXPIRATION.getValue())) {
            initFields(exclusion, ExclusionType.EXPIRATION);
        } else if (exclusion.startsWith(ExclusionType.OPTIMIZER.getValue())) {
            initFields(exclusion, ExclusionType.OPTIMIZER);
        } else if (exclusion.startsWith(ExclusionType.SHRINKER.getValue())) {
            initFields(exclusion, ExclusionType.SHRINKER);
        } else if (exclusion.startsWith(ExclusionType.SHUFFLER.getValue())) {
            initFields(exclusion, ExclusionType.SHUFFLER);
        } else if (exclusion.startsWith(ExclusionType.SOURCE_NAME.getValue())) {
            initFields(exclusion, ExclusionType.SOURCE_NAME);
        } else if (exclusion.startsWith(ExclusionType.SOURCE_DEBUG.getValue())) {
            initFields(exclusion, ExclusionType.SOURCE_DEBUG);
        } else if (exclusion.startsWith(ExclusionType.STRING_POOL.getValue())) {
            initFields(exclusion, ExclusionType.STRING_POOL);
        } else if (exclusion.startsWith(ExclusionType.RENAMER.getValue())) {
            initFields(exclusion, ExclusionType.RENAMER);
        } else {
            this.exclusion = Pattern.compile(exclusion);
            this.exclusionType = ExclusionType.GLOBAL;
        }
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

    public Pattern getExclusion() {
        return exclusion;
    }
}

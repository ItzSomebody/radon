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

package xyz.itzsomebody.radon.exclusions;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exclusion {
    private final Matcher matcher;
    private final String rawPattern;
    private final boolean invert;
    private final ExclusionType exclusionType;

    public Exclusion(String pattern) {
        this(pattern, false);
    }

    public Exclusion(String pattern, boolean invert) {
        this(pattern, ExclusionType.GLOBAL, invert);
    }

    public Exclusion(String pattern, ExclusionType exclusionType, boolean invert) {
        this.matcher = Pattern.compile(pattern).matcher("");
        this.rawPattern = pattern;
        this.invert = invert;
        this.exclusionType = exclusionType;
    }

    public boolean matches(String other, ExclusionType type) {
        if (type == ExclusionType.GLOBAL || type == exclusionType) {
            return true;
        }

        return invert != matcher.reset(other).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Exclusion) {
            return Objects.equals(rawPattern, ((Exclusion) o).rawPattern);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawPattern);
    }

    public enum ExclusionType {
        GLOBAL,
        OPTIMIZER,
        SHRINKER,
        ADD_BRIDGE_ACCESS,
        ADD_DEPRECATED_ACCESS,
        ADD_SYNTHETIC_ACCESS,
        ADD_TRASH_CLASSES,
        RENAMER,
        STRING_OBFUSCATION,
        REFERENCE_OBFUSCATION,
        NUMBER_OBFUSCATION,
        FLOW_OBFUSCATION,
        ANTI_TAMPER,
        INJECT_ANTI_DEBUGGER,
        INJECT_EXPIRATION_KILL_SWITCH,
        ANTI_MEMORY_DUMP,
        VIRTUALIZER,
        PACKER,
        MEMBER_SHUFFLER; // fixme

        public static ExclusionType forIdentifier(String identifier) {
            // This is O(n), but should be fine since this is run only during config load
            for (var type : values()) {
                if (type.name().equalsIgnoreCase(identifier)) {
                    return type;
                }
            }

            return GLOBAL;
        }
    }
}

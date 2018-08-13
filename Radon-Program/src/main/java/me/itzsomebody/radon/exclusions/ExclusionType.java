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

public enum ExclusionType {
    GLOBAL("Global"),
    STRING_ENCRYPTION("StringEncryption"),
    INVOKEDYNAMIC("InvokeDynamic"),
    FLOW_OBFUSCATION("FlowObfuscation"),
    LINE_NUMBERS("LineNumbers"),
    LOCAL_VARIABLES("LocalVariables"),
    NUMBER_OBFUSCATION("NumberObfuscation"),
    HIDE_CODE("HideCode"),
    CRASHER("Crasher"),
    EXPIRATION("Expiration"),
    OPTIMIZER("Optimizer"),
    SHRINKER("Shrinker"),
    SHUFFLER("Shuffler"),
    SOURCE_NAME("SourceName"),
    SOURCE_DEBUG("SourceDebug"),
    STRING_POOL("StringPool"),
    RENAMER("Renamer");

    private String value;

    ExclusionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

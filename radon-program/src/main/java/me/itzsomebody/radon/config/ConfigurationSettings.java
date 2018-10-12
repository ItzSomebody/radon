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

package me.itzsomebody.radon.config;

/**
 * An {@link Enum} containing all the allowed standalone configuration keys allowed.
 *
 * @author ItzSomebody
 */
public enum ConfigurationSettings {
    INPUT("Input"),
    OUTPUT("Output"),
    LIBRARIES("Libraries"),
    EXCLUSIONS("Exclusions"),
    STRING_ENCRYPTION("StringEncryption"),
    FLOW_OBFUSCATION("FlowObfuscation"),
    INVOKEDYNAMIC("InvokeDynamic"),
    LINE_NUMBERS("LineNumbers"),
    LOCAL_VARIABLES("LocalVariables"),
    NUMBER_OBFUSCATION("NumberObfuscation"),
    HIDE_CODE("HideCode"),
    CRASHER("Crasher"),
    EXPIRATION("Expiration"),
    WATERMARK("Watermarker"),
    OPTIMIZER("Optimizer"),
    SHRINKER("Shrinker"),
    SHUFFLER("Shuffler"),
    SOURCE_NAME("SourceName"),
    SOURCE_DEBUG("SourceDebug"),
    RENAMER("Renamer"),
    DICTIONARY("Dictionary"),
    TRASH_CLASSES("TrashClasses");

    private String value;

    ConfigurationSettings(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

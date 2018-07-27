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
 * Enums of config objects :D
 *
 * @author ItzSomebody
 */
public enum ConfigEnum {
    INPUT("Input"),
    OUTPUT("Output"),
    LIBRARIES("Libraries"),
    EXEMPTS("Exempts"),
    STRING_ENCRYPTION("StringEncryption"),
    FLOW_OBFUSCATION("FlowObfuscation"),
    INVOKEDYNAMIC("InvokeDynamic"),
    LOCAL_VARIABLES("LocalVariableObfuscation"),
    CRASHER("Crasher"),
    HIDER("HideCode"),
    STRING_POOL("StringPool"),
    LINE_NUMBERS("LineNumberObfuscation"),
    NUMBERS("NumberObfuscation"),
    SOURCE_NAME("SourceNameObfuscation"),
    SOURCE_DEBUG("SourceDebugObfuscation"),
    TRASH_CLASSES("TrashClasses"),
    WATERMARK_MSG("WatermarkMessage"),
    WATERMARK_KEY("WatermarkKey"),
    WATERMARK_TYPE("WatermarkType"),
    SPIGOT_PLUGIN("SpigotPlugin"), // TODO: Remove this, replace with custom rules that allow users to exclude conflicts
    RENAMER("Renamer"),
    EXPIRATION_TIME("ExpiryTime"),
    EXPIRATION_MESSAGE("ExpiryMessage"),
    SHUFFLER("Shuffler"),
    DICTIONARY("Dictionary"),
    INNERCLASSES("InnerClassRemover");

    private final String text;

    private ConfigEnum(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

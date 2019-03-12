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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An {@link Enum} containing all the allowed standalone configuration keys allowed.
 *
 * @author ItzSomebody
 */
public enum ConfigurationSetting {
    INPUT(String.class),
    OUTPUT(String.class),
    LIBRARIES(List.class),
    EXCLUSIONS(List.class),
    STRING_ENCRYPTION(Map.class),
    FLOW_OBFUSCATION(Map.class),
    REFERENCE_OBFUSCATION(Map.class),
    NUMBER_OBFUSCATION(Map.class),
    ANTI_TAMPER(String.class), // TODO
    VIRTUAL_MACHINE(Boolean.class), // TODO: ;)
    RESOURCE_ENCRYPTION(Boolean.class), // TODO
    RESOURCE_RENAMER(Boolean.class), // TODO
    //CLASS_ENCRYPTION(Map.class), // Just kidding, lol
    HIDE_CODE(Map.class),
    CRASHER(Boolean.class),
    EXPIRATION(Map.class),
    WATERMARK(Map.class),
    OPTIMIZER(Map.class),
    SHRINKER(Map.class),
    MEMBER_SHUFFLER(Boolean.class),
    RENAMER(Map.class),
    DICTIONARY(String.class),
    RANDOMIZED_STRING_LENGTH(Integer.class),
    COMPRESSION_LEVEL(Integer.class),
    VERIFY(Boolean.class),
    TRASH_CLASSES(Integer.class);

    private final Class expectedType;

    ConfigurationSetting(Class expectedType) {
        this.expectedType = expectedType;
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

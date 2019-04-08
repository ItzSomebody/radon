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

package me.itzsomebody.radon.utils;

import java.util.Map;

public class ConfigUtils {
    /**
     * Returns the specified value from the provided map.
     *
     * @param key the key to lookup the value.
     * @param map the map to lookup.
     * @param <T> generic-typing because ItzSomebody is lazy.
     * @return the specified value from the provided map.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key, Map<String, Object> map) {
        return (T) map.get(key);
    }

    // Laziness v2.0
    public static <T> T getValueOrDefault(String key, Map<String, Object> map, T defaultVal) {
        T t = getValue(key, map);

        if (t == null)
            return defaultVal;
        else
            return t;
    }
}

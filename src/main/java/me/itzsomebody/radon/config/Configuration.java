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

package me.itzsomebody.radon.config;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Wrapper around the {@link Map} from {@link Yaml#load(InputStream)}.
 *
 * @author ItzSomebody
 */
public final class Configuration {
    private final Map<String, Object> config;

    public Configuration(InputStream in) {
        config = new Yaml().load(in);
    }

    /**
     * Checks if the specified path exists in the config.
     *
     * @param path Path to check.
     * @return True if the specified path exists in the config.
     */
    public boolean contains(String path) {
        String[] parts = path.split("\\.");
        Map current = config;

        // walk down the document levels by treating them as maps until last element
        // since last element is treated as the object we want
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object result = current.get(part);
            if (!(result instanceof Map)) {
                return false;
            }

            current = (Map) result;
        }

        return current.containsKey(parts[parts.length - 1]);
    }

    /**
     * @see Configuration#contains(String)
     */
    public boolean contains(ConfigurationSetting setting) {
        return contains(setting.getConfigName());
    }

    /**
     * Get object as desired type from specified path.
     *
     * @param path Path of the object to get.
     * @param <T>  Desired type of object.
     * @return Object as desired type from specified path.
     */
    @SuppressWarnings("unchecked") // Screw type verification reeee
    public <T> T get(String path) {
        String[] parts = path.split("\\.");
        Map current = config;

        // walk down the document levels by treating them as maps until last element
        // since last element is treated as the object we want
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            current = (Map) current.get(part);
        }

        return (T) current.get(parts[parts.length - 1]);
    }

    /**
     * @see Configuration#get(String)
     */
    public <T> T get(ConfigurationSetting setting) {
        return get(setting.getConfigName());
    }

    public <T> T getOrDefault(String path, T dflt) {
        T result = get(path);

        if (result == null) {
            return dflt;
        }

        return result;
    }

    public <T> T getOrDefault(ConfigurationSetting setting, T dflt) {
        T result = get(setting);

        if (result == null) {
            return dflt;
        }

        return result;
    }
}

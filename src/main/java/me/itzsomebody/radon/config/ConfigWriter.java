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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Constructs a config in memory and writes to file.
 *
 * @author ItzSomebody
 */
public class ConfigWriter {
    /**
     * Key -> Value map.
     */
    private Map<ConfigEnum, Object> keyValueMap;

    /**
     * Lines to write to config.
     */
    private List<String> lines = new ArrayList<>();

    /**
     * Creates a new ConfigWriter object.
     *
     * @param keyValueMap Key -> Value map.
     */
    public ConfigWriter(Map<ConfigEnum, Object> keyValueMap) {
        this.keyValueMap = keyValueMap;
    }

    /**
     * Parses all options into a virtual config.
     */
    public void parseOptions() {
        for (ConfigEnum conf : ConfigEnum.values()) {
            // Get config for enum value, if it exists, add line
            Object result = keyValueMap.get(conf);
            if (result == null) {
                continue;
            }
            switch (conf) {
                case LIBRARIES:
                    List<String> libs = (List) result;
                    if (!libs.isEmpty()) {
                        lines.add("Libraries: ");
                        for (String lib : libs) {
                            lines.add("    - \"" + lib.replace("\\", "/") + "\"");
                        }
                    }
                    break;
                case EXEMPTS:
                    List<String> exempts = (List) result;
                    if (!exempts.isEmpty()) {
                        lines.add("Exempts: ");
                        for (String exempt : exempts) {
                            lines.add("    - \"" + exempt + "\"");
                        }
                    }
                    break;
                case INPUT:
                case OUTPUT:
                    lines.add(conf + ": \"" + result.toString().replace("\\", "/") + "\"");
                    break;
                default:
                    lines.add(conf + ": " + result);
            }
        }
    }

    /**
     * Writes config to a file.
     *
     * @throws IOException if the file already exists, is an output or some
     *                     other weird thing happens.
     */
    public void writeConfig(String path) throws IOException {
        File output = new File(path);
        if (output.exists())
            throw new IOException(path + " already exists!");

        if (output.isDirectory())
            throw new IOException(path + " needs to be a file, not a directory");

        output.createNewFile();
        BufferedWriter stream = new BufferedWriter(new FileWriter(output));
        for (String line : this.lines) {
            stream.write(line);
            stream.write('\n');
        }
        stream.close();
    }
}

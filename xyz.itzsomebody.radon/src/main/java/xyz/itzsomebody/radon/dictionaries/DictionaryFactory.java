/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.dictionaries;

import xyz.itzsomebody.radon.dictionaries.defined.*;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.List;

public class DictionaryFactory {
    private static final Dictionary[] DEFINED = {
            new AlphabeticalDictionary(),
            new AlphaNumericDictionary(),
            new RandomUnicodeDictionary(),
            new SpacesDictionary(),
            new UnrecognizedDictionary()
    };

    public static Dictionary forName(String name) {
        return List.of(DEFINED).stream()
                .filter(dictionary -> name.equalsIgnoreCase(dictionary.configName()))
                .findFirst()
                .orElseGet(() -> {
                    RadonLogger.info("Unknown dictionary \"" + name + "\" -- treating as user-defined charset dictionary");
                    return new CustomCharsetDictionary(name);
                });
    }

    public static Dictionary defaultDictionary() {
        return new AlphabeticalDictionary();
    }
}

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

package me.itzsomebody.radon.dictionaries;

import java.util.List;

public class DictionaryFactory {
    public static Dictionary get(String s) {
        Dictionary[] dictionaries = {
            new AlphaNumDictionary(),
            new RandomUnicodeDictionary(),
            new SpacesDictionary(),
            new UnrecognizedDictionary(),
            new CreeperDictionary()
        };

        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getDictionaryName().equals(s)) {
                return dictionary;
            }
        }

        return new CustomDictionary(s);
    }
    
    public static Dictionary getCustom(List<String> charset) {
        return new CustomDictionary(charset);
    }
}

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

package xyz.itzsomebody.radon.transformers.strings;

import xyz.itzsomebody.radon.config.Configuration;
import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.dictionaries.DictionaryFactory;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class StringTransformer extends Transformer {
    private Set<String> excludedStrings;
    protected Dictionary dictionary;

    protected boolean isExcludedString(String s) {
        return excludedStrings.contains(s);
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.STRING_OBFUSCATION;
    }

    @Override
    public void loadSetup(Configuration config) {
        // Get excluded strings
        var stringList = config.<List<String>>getOrDefault(getLocalConfigPath() + "." + StringConfigKey.EXCLUDED_STRINGS.getKey(), Collections.emptyList());
        excludedStrings = stringList.isEmpty() ? Collections.emptySet() : new HashSet<>(stringList);

        // Dictionary :O
        var dictionaryName = config.<String>get(getLocalConfigPath() + "." + StringConfigKey.DICTIONARY.getKey());
        dictionary = (dictionaryName == null) ? DictionaryFactory.defaultDictionary() : DictionaryFactory.forName(dictionaryName);
    }

    enum StringConfigKey {
        EXCLUDED_STRINGS,
        DICTIONARY;

        public String getKey() {
            return name().toLowerCase();
        }
    }
}

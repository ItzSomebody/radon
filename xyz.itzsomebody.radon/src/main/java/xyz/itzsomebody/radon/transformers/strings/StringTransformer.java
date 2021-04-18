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

import com.fasterxml.jackson.annotation.JsonProperty;
import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.dictionaries.DictionaryFactory;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;

import java.util.Set;

public abstract class StringTransformer extends Transformer {
    @JsonProperty("excluded_strings")
    private Set<String> excludedStrings;

    @JsonProperty("dictionary")
    protected Dictionary dictionary = DictionaryFactory.defaultDictionary();

    @JsonProperty("leeway")
    protected int allowedLeeway = 5000;

    protected boolean isExcludedString(String s) {
        return excludedStrings.contains(s);
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.STRING_OBFUSCATION;
    }
}

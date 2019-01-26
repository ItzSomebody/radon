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

package me.itzsomebody.radon.transformers.obfuscators.strings;

import java.util.List;
import me.itzsomebody.radon.exceptions.IllegalConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Abstract class for string encryption transformers.
 *
 * @author ItzSomebody
 */
public abstract class StringEncryption extends Transformer {
    protected StringEncryptionSetup setup;

    public StringEncryption(StringEncryptionSetup setup) {
        this.setup = setup;
    }

    protected boolean excludedString(String str) {
        for (String s : this.setup.getExemptedStrings()) {
            if (str.contains(s)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.STRING_ENCRYPTION;
    }

    public List<String> getExcludedStrings() {
        return this.setup.getExemptedStrings();
    }

    public static StringEncryption getTransformerFromString(String s, StringEncryptionSetup setup) {
        switch (s.toLowerCase()) {
            case "light": {
                return new LightStringEncryption(setup);
            }
            case "normal": {
                return new NormalStringEncryption(setup);
            }
            case "heavy": {
                return new HeavyStringEncryption(setup);
            }
            default: {
                throw new IllegalConfigurationValueException("Did not expect " + s + " as a string obfuscation mode");
            }
        }
    }
}

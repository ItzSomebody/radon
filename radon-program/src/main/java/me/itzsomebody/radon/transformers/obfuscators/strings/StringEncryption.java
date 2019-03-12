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
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Abstract class for string encryption transformers.
 *
 * @author ItzSomebody
 */
public class StringEncryption extends Transformer {
    private List<String> exemptedStrings;
    private boolean contextCheckingEnabled;
    private boolean stringPoolingEnabled;

    @Override
    public void transform() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.STRING_ENCRYPTION;
    }

    protected boolean excludedString(String str) {
        for (String s : exemptedStrings)
            if (str.contains(s))
                return true;

        return false;
    }

    public List<String> getExemptedStrings() {
        return exemptedStrings;
    }

    public void setExemptedStrings(List<String> exemptedStrings) {
        this.exemptedStrings = exemptedStrings;
    }

    public boolean isContextCheckingEnabled() {
        return contextCheckingEnabled;
    }

    public void setContextCheckingEnabled(boolean contextCheckingEnabled) {
        this.contextCheckingEnabled = contextCheckingEnabled;
    }

    public boolean isStringPoolingEnabled() {
        return stringPoolingEnabled;
    }

    public void setStringPoolingEnabled(boolean stringPoolingEnabled) {
        this.stringPoolingEnabled = stringPoolingEnabled;
    }
}

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

/**
 * Used to hold the information needed for enabling the string encryption transformers.
 *
 * @author ItzSomebody
 */
public class StringEncryptionSetup {
    private final List<String> exemptedStrings;

    public StringEncryptionSetup(List<String> exemptedStrings) {
        this.exemptedStrings = exemptedStrings;
    }

    public List<String> getExemptedStrings() {
        return this.exemptedStrings;
    }
}
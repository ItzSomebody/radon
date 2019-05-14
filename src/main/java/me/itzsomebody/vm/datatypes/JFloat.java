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

package me.itzsomebody.vm.datatypes;

public class JFloat extends JWrapper {
    private final float value;

    public JFloat(float value) {
        this.value = value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public Object asObj() {
        return value;
    }

    @Override
    public JWrapper copy() {
        return new JFloat(value);
    }
}

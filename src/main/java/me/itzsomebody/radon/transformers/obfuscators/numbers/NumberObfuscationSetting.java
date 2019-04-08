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

package me.itzsomebody.radon.transformers.obfuscators.numbers;

public enum NumberObfuscationSetting {
    CONTEXT_CHECKING(Boolean.class, new ContextCheckObfuscator()),
    ARITHMETIC_OPERATIONS(Boolean.class, new ArithmeticObfuscator()),
    BITWISE_OPERATIONS(Boolean.class, new BitwiseObfuscator()),
    DOUBLE_TAMPERING(Boolean.class, null),
    FLOAT_TAMPERING(Boolean.class, null),
    INTEGER_TAMPERING(Boolean.class, null),
    LONG_TAMPERING(Boolean.class, null);

    private final Class expectedType;
    private final NumberObfuscation numberObfuscation;

    NumberObfuscationSetting(Class expectedType, NumberObfuscation numberObfuscation) {
        this.expectedType = expectedType;
        this.numberObfuscation = numberObfuscation;
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public NumberObfuscation getNumberObfuscation() {
        return numberObfuscation;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

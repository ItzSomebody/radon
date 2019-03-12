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

package me.itzsomebody.radon.transformers.obfuscators.numbers;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Abstract class for number obfuscation transformers.
 *
 * @author ItzSomebody
 */
public class NumberObfuscation extends Transformer {
    private boolean integerTamperingEnabled;
    private boolean longTamperingEnabled;
    private boolean floatTamperingEnabled;
    private boolean doubleTamperingEnabled;

    private boolean bitwiseOperationsEnabled;
    private boolean arithmeticOperationsEnabled;

    private boolean contextCheckingEnabled;

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Number obfuscation";
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.NUMBER_OBFUSCATION;
    }

    public boolean isIntegerTamperingEnabled() {
        return integerTamperingEnabled;
    }

    public void setIntegerTamperingEnabled(boolean integerTamperingEnabled) {
        this.integerTamperingEnabled = integerTamperingEnabled;
    }

    public boolean isLongTamperingEnabled() {
        return longTamperingEnabled;
    }

    public void setLongTamperingEnabled(boolean longTamperingEnabled) {
        this.longTamperingEnabled = longTamperingEnabled;
    }

    public boolean isFloatTamperingEnabled() {
        return floatTamperingEnabled;
    }

    public void setFloatTamperingEnabled(boolean floatTamperingEnabled) {
        this.floatTamperingEnabled = floatTamperingEnabled;
    }

    public boolean isDoubleTamperingEnabled() {
        return doubleTamperingEnabled;
    }

    public void setDoubleTamperingEnabled(boolean doubleTamperingEnabled) {
        this.doubleTamperingEnabled = doubleTamperingEnabled;
    }

    public boolean isBitwiseOperationsEnabled() {
        return bitwiseOperationsEnabled;
    }

    public void setBitwiseOperationsEnabled(boolean bitwiseOperationsEnabled) {
        this.bitwiseOperationsEnabled = bitwiseOperationsEnabled;
    }

    public boolean isArithmeticOperationsEnabled() {
        return arithmeticOperationsEnabled;
    }

    public void setArithmeticOperationsEnabled(boolean arithmeticOperationsEnabled) {
        this.arithmeticOperationsEnabled = arithmeticOperationsEnabled;
    }

    public boolean isContextCheckingEnabled() {
        return contextCheckingEnabled;
    }

    public void setContextCheckingEnabled(boolean contextCheckingEnabled) {
        this.contextCheckingEnabled = contextCheckingEnabled;
    }
}

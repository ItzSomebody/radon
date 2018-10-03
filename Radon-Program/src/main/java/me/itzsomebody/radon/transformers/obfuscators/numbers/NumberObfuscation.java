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
public abstract class NumberObfuscation extends Transformer {
    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.NUMBER_OBFUSCATION;
    }

    public static NumberObfuscation getTransformerFromString(String s) {
        switch (s.toLowerCase()) {
            case "light": {
                return new LightNumberObfuscation();
            }
            case "normal": {
                return new NormalNumberObfuscation();
            }
            case "heavy": {
                return new HeavyNumberObfuscation();
            }
            default: {
                throw new RuntimeException("Did not expect " + s + " as a number obfuscation mode");
            }
        }
    }
}

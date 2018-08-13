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

package me.itzsomebody.radon.transformers.obfuscators.invokedynamic;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

public abstract class InvokeDynamic extends Transformer {
    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.INVOKEDYNAMIC;
    }

    public static InvokeDynamic getTransformerFromString(String s) {
        switch (s.toLowerCase()) {
            case "light": {
                return new LightInvokeDynamic();
            }
            case "normal": {
                return new NormalInvokeDynamic();
            }
            case "heavy": {
                return new HeavyInvokeDynamic();
            }
            default: {
                throw new RuntimeException("Did not expect " + s + " as a invokedynamic obfuscation mode");
            }
        }
    }
}

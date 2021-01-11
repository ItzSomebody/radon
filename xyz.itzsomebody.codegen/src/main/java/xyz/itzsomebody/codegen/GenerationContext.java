/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.codegen;

import xyz.itzsomebody.codegen.expressions.IRVariable;

public class GenerationContext {
    private int slotOffset = 0;

    public void setSlotOffset(int slotOffset) {
        this.slotOffset = slotOffset;
    }

    public IRVariable newVariable(WrappedType type) {
        var variable = new IRVariable(type, slotOffset);
        slotOffset += type.getType().getSize();
        return variable;
    }
}

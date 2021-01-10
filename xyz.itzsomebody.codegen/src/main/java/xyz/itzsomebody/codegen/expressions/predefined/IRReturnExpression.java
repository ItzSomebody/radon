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

package xyz.itzsomebody.codegen.expressions.predefined;

import org.objectweb.asm.Type;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRReturnExpression extends IRExpression {
    private final IRExpression target;

    public IRReturnExpression(IRExpression target) {
        super(target.getType());
        this.target = target;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock().append(target.getInstructions());
        var type = target.getType();

        switch (type.getSort()) {
            case Type.VOID:
                block.append(SimpleNode.RETURN_VOID);
                break;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                block.append(SimpleNode.RETURN_INT);
                break;
            case Type.FLOAT:
                block.append(SimpleNode.RETURN_FLOAT);
                break;
            case Type.LONG:
                block.append(SimpleNode.RETURN_LONG);
                break;
            case Type.DOUBLE:
                block.append(SimpleNode.RETURN_DOUBLE);
                break;
            default:
                block.append(SimpleNode.RETURN_OBJECT);
        }
        return block;
    }
}

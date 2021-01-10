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
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.InvokeNode;
import xyz.itzsomebody.codegen.instructions.SimpleNode;
import xyz.itzsomebody.codegen.instructions.TypeNode;

import java.util.Collections;
import java.util.List;

public class IRCastExpression extends IRExpression {
    private final IRExpression castMe;

    public IRCastExpression(IRExpression castMe, WrappedType castType) {
        super(castType);
        this.castMe = castMe;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock().append(castMe.getInstructions());
        var currentType = castMe.getType();
        var targetType = getType();

        if (currentType.equals(targetType)) {
            return block;
        }

        if (currentType.isPrimitive()) {
            if (targetType.isPrimitive()) {
                castPrimitives(block, currentType, targetType);
            } else if (targetType.isBoxed()) {
                block.append(InvokeNode.invokeStatic(targetType, "valueOf", List.of(currentType), targetType));
            } else {
                block.append(InvokeNode.invokeStatic(Utils.box(currentType), "valueOf", List.of(currentType), Utils.box(currentType)))
                        .append(TypeNode.cast(targetType));
            }
        } else if (currentType.isBoxed()) {
            if (targetType.isPrimitive()) {
                var primitiveType = currentType.getPrimitiveType();
                block.append(InvokeNode.invokeVirtual(currentType, primitiveType.getClassName() + "Value", Collections.emptyList(), primitiveType));
                castPrimitives(block, primitiveType, targetType);
            } else {
                block.append(TypeNode.cast(targetType));
            }
        } else {
            if (targetType.isPrimitive()) {
                var primitiveType = targetType.getPrimitiveType();
                block.append(TypeNode.cast(Utils.box(targetType)))
                        .append(InvokeNode.invokeVirtual(targetType, primitiveType.getClassName() + "Value", Collections.emptyList(), primitiveType));
            } else {
                block.append(TypeNode.cast(targetType));
            }
        }

        return block;
    }

    @SuppressWarnings("Duplicates")
    private static void castPrimitives(BytecodeBlock block, WrappedType source, WrappedType target) {
        var sourceSort = source.getSort();
        var targetSort = target.getSort();

        if (sourceSort == targetSort) {
            return; // Casting primitive to same primitive type = redundant
        }

        if (sourceSort == Type.BOOLEAN || sourceSort == Type.BYTE) {
            if (target.isIntType()) {
                return;
            }
            if (targetSort == Type.FLOAT) {
                block.append(SimpleNode.CAST_INT_TO_FLOAT);
                return;
            } else if (targetSort == Type.LONG) {
                block.append(SimpleNode.CAST_INT_TO_LONG);
                return;
            } else if (targetSort == Type.DOUBLE) {
                block.append(SimpleNode.CAST_INT_TO_DOUBLE);
                return;
            }
        } else if (sourceSort == Type.CHAR) {
            if (targetSort == Type.BYTE) {
                block.append(SimpleNode.CAST_INT_TO_BYTE);
                return;
            }
            if (target.isIntType()) { // Non-byte integer
                return;
            }
            if (targetSort == Type.FLOAT) {
                block.append(SimpleNode.CAST_INT_TO_FLOAT);
                return;
            } else if (targetSort == Type.LONG) {
                block.append(SimpleNode.CAST_INT_TO_LONG);
                return;
            } else if (targetSort == Type.DOUBLE) {
                block.append(SimpleNode.CAST_INT_TO_DOUBLE);
                return;
            }
        } else if (sourceSort == Type.SHORT) {
            if (targetSort == Type.BYTE) {
                block.append(SimpleNode.CAST_INT_TO_BYTE);
                return;
            }
            if (targetSort == Type.CHAR) {
                block.append(SimpleNode.CAST_INT_TO_CHAR);
                return;
            }
            if (target.isIntType()) { // Non-byte integer
                return;
            }
            if (targetSort == Type.FLOAT) {
                block.append(SimpleNode.CAST_INT_TO_FLOAT);
                return;
            } else if (targetSort == Type.LONG) {
                block.append(SimpleNode.CAST_INT_TO_LONG);
                return;
            } else if (targetSort == Type.DOUBLE) {
                block.append(SimpleNode.CAST_INT_TO_DOUBLE);
                return;
            }
        } else if (sourceSort == Type.INT) {
            if (targetSort == Type.BYTE) {
                block.append(SimpleNode.CAST_INT_TO_BYTE);
                return;
            } else if (targetSort == Type.CHAR) {
                block.append(SimpleNode.CAST_INT_TO_CHAR);
                return;
            } else if (targetSort == Type.SHORT) {
                block.append(SimpleNode.CAST_INT_TO_SHORT);
                return;
            } else if (targetSort == Type.FLOAT) {
                block.append(SimpleNode.CAST_INT_TO_FLOAT);
                return;
            } else if (targetSort == Type.LONG) {
                block.append(SimpleNode.CAST_INT_TO_LONG);
                return;
            } else if (targetSort == Type.DOUBLE) {
                block.append(SimpleNode.CAST_INT_TO_DOUBLE);
                return;
            }
        } else if (sourceSort == Type.LONG) {
            if (target.isIntType()) {
                block.append(SimpleNode.CAST_LONG_TO_INT);
                if (targetSort == Type.BYTE) {
                    block.append(SimpleNode.CAST_INT_TO_BYTE);
                } else if (targetSort == Type.CHAR) {
                    block.append(SimpleNode.CAST_INT_TO_CHAR);
                } else if (targetSort == Type.SHORT) {
                    block.append(SimpleNode.CAST_INT_TO_SHORT);
                }
                return;
            } else if (targetSort == Type.FLOAT) {
                block.append(SimpleNode.CAST_LONG_TO_FLOAT);
                return;
            } else if (targetSort == Type.DOUBLE) {
                block.append(SimpleNode.CAST_LONG_TO_DOUBLE);
                return;
            }
        } else if (sourceSort == Type.FLOAT) {
            if (target.isIntType()) {
                block.append(SimpleNode.CAST_FLOAT_TO_INT);
                if (targetSort == Type.BYTE) {
                    block.append(SimpleNode.CAST_INT_TO_BYTE);
                } else if (targetSort == Type.CHAR) {
                    block.append(SimpleNode.CAST_INT_TO_CHAR);
                } else if (targetSort == Type.SHORT) {
                    block.append(SimpleNode.CAST_INT_TO_SHORT);
                }
                return;
            } else if (targetSort == Type.LONG) {
                block.append(SimpleNode.CAST_FLOAT_TO_LONG);
                return;
            } else if (targetSort == Type.DOUBLE) {
                block.append(SimpleNode.CAST_FLOAT_TO_DOUBLE);
                return;
            }
        } else if (sourceSort == Type.DOUBLE) {
            if (target.isIntType()) {
                block.append(SimpleNode.CAST_DOUBLE_TO_INT);
                if (targetSort == Type.BYTE) {
                    block.append(SimpleNode.CAST_INT_TO_BYTE);
                } else if (targetSort == Type.CHAR) {
                    block.append(SimpleNode.CAST_INT_TO_CHAR);
                } else if (targetSort == Type.SHORT) {
                    block.append(SimpleNode.CAST_INT_TO_SHORT);
                }
                return;
            } else if (targetSort == Type.FLOAT) {
                block.append(SimpleNode.CAST_DOUBLE_TO_FLOAT);
                return;
            } else if (targetSort == Type.LONG) {
                block.append(SimpleNode.CAST_DOUBLE_TO_LONG);
                return;
            }
        }
        throw new UncompilableNodeException("Cannot cast " + source + " to " + target);
    }
}

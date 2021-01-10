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

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.ConstantNode;

import java.util.List;

public class IRConstantDynamicExpression extends IRExpression {
    private final String name;
    private final WrappedType type;
    private final WrappedHandle bootstrap;
    private final List<ConstantNode> bootstrapArgs;

    public IRConstantDynamicExpression(String name, WrappedType type, WrappedHandle bootstrap, List<ConstantNode> bootstrapArgs) {
        super(type);
        this.name = name;
        this.type = type;
        this.bootstrap = bootstrap;
        this.bootstrapArgs = bootstrapArgs;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock().append(ConstantNode.dynamicConst(name, type, bootstrap, bootstrapArgs));
    }
}

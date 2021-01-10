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

import org.objectweb.asm.tree.InsnList;
import xyz.itzsomebody.codegen.instructions.CompilableNode;

import java.util.ArrayList;
import java.util.List;

public class BytecodeBlock {
    private final List<CompilableNode> nodes = new ArrayList<>();

    public BytecodeBlock append(BytecodeBlock block) {
        nodes.addAll(block.nodes);
        return this;
    }

    public BytecodeBlock append(CompilableNode node) {
        nodes.add(node);
        return this;
    }

    public InsnList compile() {
        var insns = new InsnList();
        nodes.forEach(node -> insns.add(node.getNode()));
        return insns;
    }

    public List<CompilableNode> getNodes() {
        return nodes;
    }
}

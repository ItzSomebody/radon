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

package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import xyz.itzsomebody.codegen.Utils;

import java.util.ArrayList;

public class SwitchNode implements CompilableNode {
    private final ArrayList<Integer> keys;
    private final ArrayList<BytecodeLabel> labels;
    private final BytecodeLabel defaultLabel;

    public SwitchNode(ArrayList<Integer> keys, ArrayList<BytecodeLabel> labels, BytecodeLabel defaultLabel) {
        this.keys = keys;
        this.labels = labels;
        this.defaultLabel = defaultLabel;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new LookupSwitchInsnNode(defaultLabel.getLabel(), keys.stream().mapToInt(i -> i).toArray(), Utils.unwrapLabels(labels).toArray(new LabelNode[0]));
    }
}

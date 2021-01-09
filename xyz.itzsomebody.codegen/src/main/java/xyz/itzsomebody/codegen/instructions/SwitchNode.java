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

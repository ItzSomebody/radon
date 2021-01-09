package xyz.itzsomebody.codegen;

import org.objectweb.asm.tree.LabelNode;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String unwrapMethodDescriptor(List<WrappedType> parameterTypes, WrappedType returnType) {
        var sb = new StringBuilder("(");
        parameterTypes.forEach(type -> sb.append(type.unwrap()));
        sb.append(')').append(returnType.unwrap());
        return sb.toString();
    }

    public static ArrayList<LabelNode> unwrapLabels(ArrayList<BytecodeLabel> wrappedLabels) {
        var unwrappedLabels = new ArrayList<LabelNode>(wrappedLabels.size());
        wrappedLabels.forEach(wrappedLabel -> unwrappedLabels.add(wrappedLabel.getLabel()));
        return unwrappedLabels;
    }
}

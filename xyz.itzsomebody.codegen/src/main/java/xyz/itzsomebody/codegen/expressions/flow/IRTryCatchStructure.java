package xyz.itzsomebody.codegen.expressions.flow;

import org.objectweb.asm.tree.TryCatchBlockNode;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.JumpNode;

public class IRTryCatchStructure extends IRFlowStructure {
    private final BytecodeBlock trapRange;
    private final BytecodeBlock handler;
    private final WrappedType exceptionType;
    private final BytecodeLabel trapStartLabel = new BytecodeLabel();
    private final BytecodeLabel trapEndLabel = new BytecodeLabel();
    private final BytecodeLabel handlerLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRTryCatchStructure(BytecodeBlock trapRange, BytecodeBlock handler, WrappedType exceptionType) {
        this.trapRange = trapRange;
        this.handler = handler;
        this.exceptionType =exceptionType;
    }

    public TryCatchBlockNode getTryCatchBlocKNode() {
        return new TryCatchBlockNode(trapStartLabel.getLabel(), trapEndLabel.getLabel(), handlerLabel.getLabel(), exceptionType.getInternalName());
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                // Try block
                .append(trapStartLabel)
                .append(trapRange)
                .append(trapEndLabel)
                .append(JumpNode.jumpUnconditionally(exitLabel)) // No exception has been thrown so skip the handler

                // Catch block
                .append(handlerLabel)
                .append(handler)

                // Exit
                .append(exitLabel);
    }

    public BytecodeLabel getTrapStartLabel() {
        return trapStartLabel;
    }

    public BytecodeLabel getTrapEndLabel() {
        return trapEndLabel;
    }

    public BytecodeLabel getHandlerLabel() {
        return handlerLabel;
    }

    public BytecodeLabel getExitLabel() {
        return exitLabel;
    }
}

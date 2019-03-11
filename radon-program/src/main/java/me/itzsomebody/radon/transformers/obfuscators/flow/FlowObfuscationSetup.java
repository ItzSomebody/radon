package me.itzsomebody.radon.transformers.obfuscators.flow;

public class FlowObfuscationSetup {
    private boolean replaceGoto;
    private boolean insertBogusJumps;
    private boolean rearrangeFlow;
    private boolean fakeCatchBlocks;
    private boolean mutilateNullCheck;
    private boolean combineTryWithCatch;

    public void setReplaceGoto(boolean replaceGoto) {
        this.replaceGoto = replaceGoto;
    }

    public boolean isReplaceGoto() {
        return replaceGoto;
    }

    public void setInsertBogusJumps(boolean insertBogusJumps) {
        this.insertBogusJumps = insertBogusJumps;
    }

    public boolean isInsertBogusJumps() {
        return insertBogusJumps;
    }

    public void setRearrangeFlow(boolean rearrangeFlow) {
        this.rearrangeFlow = rearrangeFlow;
    }

    public boolean isRearrangeFlow() {
        return rearrangeFlow;
    }

    public void setFakeCatchBlocks(boolean fakeCatchBlocks) {
        this.fakeCatchBlocks = fakeCatchBlocks;
    }

    public boolean isFakeCatchBlocks() {
        return fakeCatchBlocks;
    }

    public void setMutilateNullCheck(boolean mutilateNullCheck) {
        this.mutilateNullCheck = mutilateNullCheck;
    }

    public boolean isMutilateNullCheck() {
        return mutilateNullCheck;
    }

    public void setCombineTryWithCatch(boolean combineTryWithCatch) {
        this.combineTryWithCatch = combineTryWithCatch;
    }

    public boolean isCombineTryWithCatch() {
        return combineTryWithCatch;
    }
}

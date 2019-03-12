/*
 * Copyright (C) 2018 ItzSomebody
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

package me.itzsomebody.radon.transformers.obfuscators.flow;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Abstract class for flow obfuscation transformers.
 *
 * @author ItzSomebody
 */
public class FlowObfuscation extends Transformer {
    private boolean replaceGotoEnabled;
    private boolean insertBogusJumpsEnabled;
    private boolean rearrangeFlowEnabled;
    private boolean fakeCatchBlocksEnabled;
    private boolean mutilateNullCheckEnabled;
    private boolean combineTryWithCatchEnabled;

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Flow Obfuscation";
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.FLOW_OBFUSCATION;
    }

    public boolean isReplaceGotoEnabled() {
        return replaceGotoEnabled;
    }

    public void setReplaceGotoEnabled(boolean replaceGotoEnabled) {
        this.replaceGotoEnabled = replaceGotoEnabled;
    }

    public boolean isInsertBogusJumpsEnabled() {
        return insertBogusJumpsEnabled;
    }

    public void setInsertBogusJumpsEnabled(boolean insertBogusJumpsEnabled) {
        this.insertBogusJumpsEnabled = insertBogusJumpsEnabled;
    }

    public boolean isRearrangeFlowEnabled() {
        return rearrangeFlowEnabled;
    }

    public void setRearrangeFlowEnabled(boolean rearrangeFlowEnabled) {
        this.rearrangeFlowEnabled = rearrangeFlowEnabled;
    }

    public boolean isFakeCatchBlocksEnabled() {
        return fakeCatchBlocksEnabled;
    }

    public void setFakeCatchBlocksEnabled(boolean fakeCatchBlocksEnabled) {
        this.fakeCatchBlocksEnabled = fakeCatchBlocksEnabled;
    }

    public boolean isMutilateNullCheckEnabled() {
        return mutilateNullCheckEnabled;
    }

    public void setMutilateNullCheckEnabled(boolean mutilateNullCheckEnabled) {
        this.mutilateNullCheckEnabled = mutilateNullCheckEnabled;
    }

    public boolean isCombineTryWithCatchEnabled() {
        return combineTryWithCatchEnabled;
    }

    public void setCombineTryWithCatchEnabled(boolean combineTryWithCatchEnabled) {
        this.combineTryWithCatchEnabled = combineTryWithCatchEnabled;
    }
}

/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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

public enum FlowObfuscationSetting {
    REPLACE_GOTO(Boolean.class, new GotoReplacer()),
    INSERT_BOGUS_JUMPS(Boolean.class, new BogusJumpInserter()),
    REARRANGE_BLOCKS(Boolean.class, new BlockRearranger()),
    FAKE_CATCH_BLOCKS(Boolean.class, new FakeCatchBlocks()),
    MUTILATE_NULL_CHECK(Boolean.class, new NullCheckMutilator()),
    COMBINE_TRY_WITH_CATCH(Boolean.class, new TryCatchCombiner());

    private final Class expectedType;
    private final FlowObfuscation flowObfuscation;

    FlowObfuscationSetting(Class expectedType, FlowObfuscation flowObfuscation) {
        this.expectedType = expectedType;
        this.flowObfuscation = flowObfuscation;
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public FlowObfuscation getFlowObfuscation() {
        return flowObfuscation;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

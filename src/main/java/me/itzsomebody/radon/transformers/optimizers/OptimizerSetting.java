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

package me.itzsomebody.radon.transformers.optimizers;

public enum OptimizerSetting {
    INLINE_GOTO_GOTO(Boolean.class, new GotoGotoInliner()),
    INLINE_GOTO_RETURN(Boolean.class, new GotoReturnInliner()),
    REMOVE_NOPS(Boolean.class, new NopRemover());

    private final Class expectedType;
    private final Optimizer optimizer;

    OptimizerSetting(Class expectedType, Optimizer optimizer) {
        this.expectedType = expectedType;
        this.optimizer = optimizer;
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

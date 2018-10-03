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

package me.itzsomebody.radon.transformers.optimizers;

/**
 * Enables and runs the optimization transformers.
 *
 * @author ItzSomebody
 */
public class OptimizerDelegator extends Optimizer {
    private OptimizerSetup setup;

    public OptimizerDelegator(OptimizerSetup setup) {
        this.setup = setup;
    }

    @Override
    public void transform() {
        if (this.setup.isNopRemoverEnabled()) {
            NopRemover nopRemover = new NopRemover();
            nopRemover.init(this.radon);
            nopRemover.transform();
        }
        if (this.setup.isGotoGotoEnabled()) {
            GotoGotoRemover gotoGotoRemover = new GotoGotoRemover();
            gotoGotoRemover.init(this.radon);
            gotoGotoRemover.transform();
        }
        if (this.setup.isGotoReturnEnabled()) {
            GotoReturnRemover gotoReturnRemover = new GotoReturnRemover();
            gotoReturnRemover.init(this.radon);
            gotoReturnRemover.transform();
        }
    }

    @Override
    public String getName() {
        return "Optimizer";
    }

    public OptimizerSetup getSetup() {
        return setup;
    }
}

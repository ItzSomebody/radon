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

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Abstract class for optimization transformers.
 *
 * @author ItzSomebody
 */
public class Optimizer extends Transformer {
    private boolean removeNopsEnabled;
    private boolean inlineGotoGotosEnabled;
    private boolean inlineGotoReturnEnabled;
    // TODO Add some more inliners cuz why not

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Optimizer";
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.OPTIMIZER;
    }

    public boolean isRemoveNopsEnabled() {
        return removeNopsEnabled;
    }

    public void setRemoveNopsEnabled(boolean removeNopsEnabled) {
        this.removeNopsEnabled = removeNopsEnabled;
    }

    public boolean isInlineGotoGotosEnabled() {
        return inlineGotoGotosEnabled;
    }

    public void setInlineGotoGotosEnabled(boolean inlineGotoGotosEnabled) {
        this.inlineGotoGotosEnabled = inlineGotoGotosEnabled;
    }

    public boolean isInlineGotoReturnEnabled() {
        return inlineGotoReturnEnabled;
    }

    public void setInlineGotoReturnEnabled(boolean inlineGotoReturnEnabled) {
        this.inlineGotoReturnEnabled = inlineGotoReturnEnabled;
    }
}

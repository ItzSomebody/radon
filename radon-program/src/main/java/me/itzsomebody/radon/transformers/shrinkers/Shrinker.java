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

package me.itzsomebody.radon.transformers.shrinkers;

import java.util.ArrayList;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Abstract class for shrinking transformers.
 *
 * @author ItzSomebody
 */
public class Shrinker extends Transformer {
    private boolean removeDeprecatedEnabled;
    private boolean removeInnerClassesEnabled;
    private boolean removeLineNumbersEnabled;
    private boolean removeLocalVarsEnabled;
    private boolean removeOuterMethodEnabled;
    private boolean removeSignatureEnabled;
    private boolean removeSourceDebugEnabled;
    private boolean removeSourceFileEnabled;
    private boolean removeSyntheticEnabled;
    private boolean removeUnknownAttributesEnabled;

    private boolean removeInvisibleAnnotationsEnabled;
    private boolean removeInvisibleParametersAnnotationsEnabled;
    private boolean removeInvisibleTypeAnnotationsEnabled;

    private boolean removeVisibleAnnotationsEnabled;
    private boolean removeVisibleParametersAnnotationsEnabled;
    private boolean removeVisibleTypeAnnotationsEnabled;

    @Override
    public void transform() {
        ArrayList<Shrinker> shrinkers = new ArrayList<>();

        if (isRemoveDeprecatedEnabled())
            shrinkers.add(new DeprecatedAccessRemover());

        if (isRemoveInnerClassesEnabled())
            shrinkers.add(new InnerClassesRemover());

        if (isRemoveLineNumbersEnabled())
            shrinkers.add(new LineNumberRemover());

        if (isRemoveLocalVarsEnabled())
            shrinkers.add(new LocalVariableRemover());

        if (isRemoveOuterMethodEnabled())
            shrinkers.add(new OuterMethodRemover());

        if (isRemoveSignatureEnabled())
            shrinkers.add(new SignatureRemover());

        if (isRemoveSourceDebugEnabled())
            shrinkers.add(new SourceDebugRemover());

        if (isRemoveSourceFileEnabled())
            shrinkers.add(new SourceFileRemover());

        if (isRemoveSyntheticEnabled())
            shrinkers.add(new SyntheticAccessRemover());

        if (isRemoveUnknownAttributesEnabled())
            shrinkers.add(new UnknownAttributesRemover());

        if (isRemoveInvisibleAnnotationsEnabled())
            shrinkers.add(new InvisibleAnnotationsRemover());

        if (isRemoveInvisibleParametersAnnotationsEnabled())
            shrinkers.add(new InvisibleParameterAnnotationsRemover());

        if (isRemoveInvisibleTypeAnnotationsEnabled())
            shrinkers.add(new InvisibleTypeAnnotationsRemover());

        if (isRemoveVisibleAnnotationsEnabled())
            shrinkers.add(new InvisibleAnnotationsRemover());

        if (isRemoveVisibleParametersAnnotationsEnabled())
            shrinkers.add(new InvisibleParameterAnnotationsRemover());

        if (isRemoveVisibleTypeAnnotationsEnabled())
            shrinkers.add(new InvisibleTypeAnnotationsRemover());

        shrinkers.forEach(shrinker -> {
            shrinker.init(radon);
            shrinker.transform();
        });
    }

    @Override
    public String getName() {
        return "Shrinker";
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.SHRINKER;
    }

    public boolean isRemoveDeprecatedEnabled() {
        return removeDeprecatedEnabled;
    }

    public void setRemoveDeprecatedEnabled(boolean removeDeprecatedEnabled) {
        this.removeDeprecatedEnabled = removeDeprecatedEnabled;
    }

    public boolean isRemoveInnerClassesEnabled() {
        return removeInnerClassesEnabled;
    }

    public void setRemoveInnerClassesEnabled(boolean removeInnerClassesEnabled) {
        this.removeInnerClassesEnabled = removeInnerClassesEnabled;
    }

    public boolean isRemoveLineNumbersEnabled() {
        return removeLineNumbersEnabled;
    }

    public void setRemoveLineNumbersEnabled(boolean removeLineNumbersEnabled) {
        this.removeLineNumbersEnabled = removeLineNumbersEnabled;
    }

    public boolean isRemoveLocalVarsEnabled() {
        return removeLocalVarsEnabled;
    }

    public void setRemoveLocalVarsEnabled(boolean removeLocalVarsEnabled) {
        this.removeLocalVarsEnabled = removeLocalVarsEnabled;
    }

    public boolean isRemoveOuterMethodEnabled() {
        return removeOuterMethodEnabled;
    }

    public void setRemoveOuterMethodEnabled(boolean removeOuterMethodEnabled) {
        this.removeOuterMethodEnabled = removeOuterMethodEnabled;
    }

    public boolean isRemoveSignatureEnabled() {
        return removeSignatureEnabled;
    }

    public void setRemoveSignatureEnabled(boolean removeSignatureEnabled) {
        this.removeSignatureEnabled = removeSignatureEnabled;
    }

    public boolean isRemoveSourceDebugEnabled() {
        return removeSourceDebugEnabled;
    }

    public void setRemoveSourceDebugEnabled(boolean removeSourceDebugEnabled) {
        this.removeSourceDebugEnabled = removeSourceDebugEnabled;
    }

    public boolean isRemoveSourceFileEnabled() {
        return removeSourceFileEnabled;
    }

    public void setRemoveSourceFileEnabled(boolean removeSourceFileEnabled) {
        this.removeSourceFileEnabled = removeSourceFileEnabled;
    }

    public boolean isRemoveSyntheticEnabled() {
        return removeSyntheticEnabled;
    }

    public void setRemoveSyntheticEnabled(boolean removeSyntheticEnabled) {
        this.removeSyntheticEnabled = removeSyntheticEnabled;
    }

    public boolean isRemoveUnknownAttributesEnabled() {
        return removeUnknownAttributesEnabled;
    }

    public void setRemoveUnknownAttributesEnabled(boolean removeUnknownAttributesEnabled) {
        this.removeUnknownAttributesEnabled = removeUnknownAttributesEnabled;
    }

    public boolean isRemoveInvisibleAnnotationsEnabled() {
        return removeInvisibleAnnotationsEnabled;
    }

    public void setRemoveInvisibleAnnotationsEnabled(boolean removeInvisibleAnnotationsEnabled) {
        this.removeInvisibleAnnotationsEnabled = removeInvisibleAnnotationsEnabled;
    }

    public boolean isRemoveInvisibleParametersAnnotationsEnabled() {
        return removeInvisibleParametersAnnotationsEnabled;
    }

    public void setRemoveInvisibleParametersAnnotationsEnabled(boolean removeInvisibleParametersAnnotationsEnabled) {
        this.removeInvisibleParametersAnnotationsEnabled = removeInvisibleParametersAnnotationsEnabled;
    }

    public boolean isRemoveInvisibleTypeAnnotationsEnabled() {
        return removeInvisibleTypeAnnotationsEnabled;
    }

    public void setRemoveInvisibleTypeAnnotationsEnabled(boolean removeInvisibleTypeAnnotationsEnabled) {
        this.removeInvisibleTypeAnnotationsEnabled = removeInvisibleTypeAnnotationsEnabled;
    }

    public boolean isRemoveVisibleAnnotationsEnabled() {
        return removeVisibleAnnotationsEnabled;
    }

    public void setRemoveVisibleAnnotationsEnabled(boolean removeVisibleAnnotationsEnabled) {
        this.removeVisibleAnnotationsEnabled = removeVisibleAnnotationsEnabled;
    }

    public boolean isRemoveVisibleParametersAnnotationsEnabled() {
        return removeVisibleParametersAnnotationsEnabled;
    }

    public void setRemoveVisibleParametersAnnotationsEnabled(boolean removeVisibleParametersAnnotationsEnabled) {
        this.removeVisibleParametersAnnotationsEnabled = removeVisibleParametersAnnotationsEnabled;
    }

    public boolean isRemoveVisibleTypeAnnotationsEnabled() {
        return removeVisibleTypeAnnotationsEnabled;
    }

    public void setRemoveVisibleTypeAnnotationsEnabled(boolean removeVisibleTypeAnnotationsEnabled) {
        this.removeVisibleTypeAnnotationsEnabled = removeVisibleTypeAnnotationsEnabled;
    }
}

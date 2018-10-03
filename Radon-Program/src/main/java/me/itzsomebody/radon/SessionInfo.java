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

package me.itzsomebody.radon;

import java.io.File;
import java.util.List;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.transformers.Transformer;

public class SessionInfo {
    private File input;
    private File output;
    private List<File> libraries;
    private List<Transformer> transformers;
    private ExclusionManager exclusions;
    private int trashClasses;
    private Dictionaries dictionaryType;

    public void setInput(File input) {
        this.input = input;
    }

    public File getInput() {
        return this.input;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getOutput() {
        return this.output;
    }

    public void setLibraries(List<File> libraries) {
        this.libraries = libraries;
    }

    public List<File> getLibraries() {
        return libraries;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    public List<Transformer> getTransformers() {
        return this.transformers;
    }

    public void setExclusions(ExclusionManager exclusions) {
        this.exclusions = exclusions;
    }

    public ExclusionManager getExclusionManager() {
        return this.exclusions;
    }

    public void setTrashClasses(int trashClasses) {
        this.trashClasses = trashClasses;
    }

    public int getTrashClasses() {
        return this.trashClasses;
    }

    public void setDictionaryType(Dictionaries dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    public Dictionaries getDictionaryType() {
        return this.dictionaryType;
    }
}

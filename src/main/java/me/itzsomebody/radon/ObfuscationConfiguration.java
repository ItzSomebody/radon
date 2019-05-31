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

package me.itzsomebody.radon;

import java.io.File;
import java.util.List;
import me.itzsomebody.radon.dictionaries.Dictionary;
import me.itzsomebody.radon.exclusions.ExclusionManager;
import me.itzsomebody.radon.transformers.Transformer;

public class ObfuscationConfiguration {
    private File input;
    private File output;
    private List<File> libraries;
    private List<Transformer> transformers;
    private ExclusionManager exclusionManager;
    private int nTrashClasses;
    private int randomizedStringLength;
    private Dictionary dictionary;
    private int compressionLevel;
    private boolean verify;
    private boolean corruptCrc;

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public List<File> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<File> libraries) {
        this.libraries = libraries;
    }

    public List<Transformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    public ExclusionManager getExclusionManager() {
        return exclusionManager;
    }

    public void setExclusionManager(ExclusionManager exclusionManager) {
        this.exclusionManager = exclusionManager;
    }

    public int getnTrashClasses() {
        return nTrashClasses;
    }

    public void setnTrashClasses(int nTrashClasses) {
        this.nTrashClasses = nTrashClasses;
    }

    public int getRandomizedStringLength() {
        return randomizedStringLength;
    }

    public void setRandomizedStringLength(int randomizedStringLength) {
        this.randomizedStringLength = randomizedStringLength;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public boolean isVerify() {
        return verify; // TODO
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public boolean isCorruptCrc() {
        return corruptCrc;
    }

    public void setCorruptCrc(boolean corruptCrc) {
        this.corruptCrc = corruptCrc;
    }
}

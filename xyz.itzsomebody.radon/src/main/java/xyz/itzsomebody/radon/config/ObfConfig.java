/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.exclusions.ExclusionManager;
import xyz.itzsomebody.radon.transformers.Transformer;

import java.util.Collections;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Provides all of the information needed for Radon to do its job.
 *
 * @author itzsomebody
 */
public class ObfConfig {
    @JsonProperty("input")
    public String input;

    @JsonProperty("output")
    public String output;

    @JsonProperty("libraries")
    public List<String> libraries;

    @JsonProperty("exclusions")
    public ExclusionManager exclusions = new ExclusionManager(Collections.emptyMap());

    @JsonProperty("transformers")
    public List<Transformer> transformers;

    @JsonProperty("use_store")
    public boolean useStore;

    @JsonProperty("compression_level")
    public int compressionLevel = Deflater.BEST_COMPRESSION;

    @JsonProperty("zip_comment")
    public String zipComment = RadonConstants.DEFAULT_ZIP_COMMENT;

    @JsonProperty("verify")
    public boolean verify;

    @JsonProperty("fake_duplicate_entries")
    public int fakeDuplicateEntries;

    @JsonProperty("corrupt_crcs")
    public boolean corruptCrcs;

    @JsonProperty("anti_extraction")
    public boolean antiExtraction;

    @JsonProperty("attempt_compute_maxs")
    public boolean attemptComputeMaxs;
}

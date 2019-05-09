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

package me.itzsomebody.radon.config;

import java.util.List;
import java.util.Map;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.transformers.miscellaneous.Packer;
import me.itzsomebody.radon.transformers.miscellaneous.expiration.Expiration;
import me.itzsomebody.radon.transformers.miscellaneous.watermarker.Watermarker;
import me.itzsomebody.radon.transformers.obfuscators.AntiTamper;
import me.itzsomebody.radon.transformers.obfuscators.InstructionSetReducer;
import me.itzsomebody.radon.transformers.obfuscators.ResourceRenamer;
import me.itzsomebody.radon.transformers.obfuscators.flow.FlowObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.hidecode.HideCode;
import me.itzsomebody.radon.transformers.obfuscators.numbers.NumberObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.references.ReferenceObfuscation;
import me.itzsomebody.radon.transformers.obfuscators.renamer.Renamer;
import me.itzsomebody.radon.transformers.obfuscators.shuffler.MemberShuffler;
import me.itzsomebody.radon.transformers.obfuscators.strings.StringEncryption;
import me.itzsomebody.radon.transformers.obfuscators.virtualizer.Virtualizer;
import me.itzsomebody.radon.transformers.optimizers.Optimizer;
import me.itzsomebody.radon.transformers.shrinkers.Shrinker;

/**
 * An {@link Enum} containing all the allowed standalone configuration keys allowed.
 *
 * @author ItzSomebody
 */
public enum ConfigurationSetting {
    INPUT(String.class, null),
    OUTPUT(String.class, null),
    LIBRARIES(List.class, null),
    EXCLUSIONS(List.class, null),
    STRING_ENCRYPTION(Map.class, new StringEncryption()),
    FLOW_OBFUSCATION(Map.class, new FlowObfuscation()),
    REFERENCE_OBFUSCATION(Map.class, new ReferenceObfuscation()),
    NUMBER_OBFUSCATION(Map.class, new NumberObfuscation()),
    ANTI_TAMPER(Boolean.class, new AntiTamper()),
    INSTRUCTION_SET_REDUCER(Boolean.class, new InstructionSetReducer()),
    VIRTUALIZER(Boolean.class, new Virtualizer()), // TODO: ;)
    RESOURCE_RENAMER(Boolean.class, new ResourceRenamer()), // TODO
    PACKER(Boolean.class, new Packer()),
    //CLASS_ENCRYPTION(Map.class, new ClassEncryption()), // Just kidding, lol
    HIDE_CODE(Map.class, new HideCode()),
    //CRASHER(Boolean.class, new Crasher()),
    EXPIRATION(Map.class, new Expiration()),
    WATERMARK(Map.class, new Watermarker()),
    OPTIMIZER(Map.class, new Optimizer()),
    SHRINKER(Map.class, new Shrinker()),
    MEMBER_SHUFFLER(Map.class, new MemberShuffler()),
    RENAMER(Map.class, new Renamer()),
    DICTIONARY(String.class, null),
    RANDOMIZED_STRING_LENGTH(Integer.class, null),
    COMPRESSION_LEVEL(Integer.class, null),
    VERIFY(Boolean.class, null),
    CORRUPT_CRC(Boolean.class, null),
    TRASH_CLASSES(Integer.class, null);

    private final Class expectedType;
    private final Transformer transformer;

    ConfigurationSetting(Class expectedType, Transformer transformer) {
        this.expectedType = expectedType;
        this.transformer = transformer;
    }

    /**
     * Returns the expected class type of the key represented by this {@link ConfigurationSetting}.
     *
     * @return expected class type of the key represented by this {@link ConfigurationSetting}.
     */
    public Class getExpectedType() {
        return expectedType;
    }

    /**
     * Returns the corresponding transformer to this {@link ConfigurationSetting} if any.
     *
     * @return The corresponding transformer to this {@link ConfigurationSetting} if any.
     */
    public Transformer getTransformer() {
        return transformer;
    }

    /**
     * Returns the name of this Enum constant in lowercase.
     *
     * @return the name of this Enum constant in lowercase.
     */
    public String getName() {
        return name().toLowerCase();
    }
}

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

import xyz.itzsomebody.radon.RadonConstants;
import xyz.itzsomebody.radon.exceptions.FatalRadonException;
import xyz.itzsomebody.radon.exceptions.PreventableRadonException;
import xyz.itzsomebody.radon.exclusions.ExclusionManager;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * Provides all of the information needed for Radon to do its job.
 *
 * @author itzsomebody
 */
public class ObfConfig {
    private final Map<String, Object> lookup = new HashMap<>();
    private Configuration config;

    public static ObfConfig from(Configuration config) {
        var obfConfig = new ObfConfig();

        // In case this is ever needed
        obfConfig.config = config;

        // Input / Output / Libraries
        obfConfig.put(Key.INPUT, config.get(Key.INPUT.getKeyString()));
        obfConfig.put(Key.OUTPUT, config.get(Key.OUTPUT.getKeyString()));
        obfConfig.put(Key.LIBRARIES, config.getOrDefault(Key.LIBRARIES.getKeyString(), Collections.emptyList()));

        // Exclusions
        var patterns = config.getOrDefault(Key.EXCLUSIONS.getKeyString(), Collections.<String, String>emptyMap());
        obfConfig.put(Key.EXCLUSIONS, new ExclusionManager(patterns));

        // Transformers
        var transformers = new ArrayList<Transformer>();
        var listedTransformers = config.getOrDefault(Key.TRANSFORMERS.getKeyString(), Collections.<String, Object>emptyMap());
        listedTransformers.forEach((k, v) -> {
            try {
                // Typically, it's bad to use Enum#valueOf(Object) but should be fine here as performance impact is minimal
                // since we're just loading the config and not obfuscating anything yet.
                Transformers transformerEnum = Transformers.valueOf(k.toUpperCase());
                Constructor<? extends Transformer> transformerConstructor = transformerEnum.getTransformerClass().getConstructor();
                transformerConstructor.setAccessible(true);
                Transformer transformer = transformerConstructor.newInstance();
                if (!(v instanceof Boolean) || !((boolean) v)) {
                    transformer.loadSetup(config);
                }
                transformers.add(transformer);
            } catch (IllegalArgumentException e) {
                throw new PreventableRadonException("Transformer \"" + k + "\" is an unknown transformer");
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new FatalRadonException(e);
            } catch (InvocationTargetException e) {
                throw new FatalRadonException(e.getTargetException());
            }
        });
        obfConfig.put(Key.TRANSFORMERS, transformers);

        // Misc.
        obfConfig.put(Key.USE_STORE, config.getOrDefault(Key.USE_STORE.getKeyString(), false));
        obfConfig.put(Key.COMPRESSION_LEVEL, config.getOrDefault(Key.COMPRESSION_LEVEL.getKeyString(), Deflater.BEST_COMPRESSION));
        obfConfig.put(Key.ZIP_COMMENT, config.getOrDefault(Key.ZIP_COMMENT.getKeyString(), RadonConstants.DEFAULT_ZIP_COMMENT));
        obfConfig.put(Key.VERIFY, config.getOrDefault(Key.VERIFY.getKeyString(), false));
        obfConfig.put(Key.FAKE_DUPLICATE_ENTRIES, config.getOrDefault(Key.FAKE_DUPLICATE_ENTRIES.getKeyString(), 0));
        obfConfig.put(Key.CORRUPT_CRCS, config.getOrDefault(Key.CORRUPT_CRCS.getKeyString(), false));
        obfConfig.put(Key.ANTI_EXTRACTION, config.getOrDefault(Key.ANTI_EXTRACTION.getKeyString(), false));
        obfConfig.put(Key.ATTEMPT_COMPUTE_MAXS, config.getOrDefault(Key.ATTEMPT_COMPUTE_MAXS.getKeyString(), false));

        return obfConfig;
    }

    /**
     * Associates the given key with the given value in {@link ObfConfig#lookup} via {@link Map#put(Object, Object)}.
     */
    private void put(Key key, Object value) {
        lookup.put(key.getKeyString(), value);
    }

    /**
     * Returns associated value for a specified key.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            return (T) lookup.get(key);
        } catch (ClassCastException e) {
            throw new PreventableRadonException("Type exception when getting " + key + ":" + e);
        }
    }

    public Configuration getConfig() {
        return config;
    }

    public enum Key {
        INPUT,
        OUTPUT,
        LIBRARIES,
        EXCLUSIONS,
        TRANSFORMERS,
        USE_STORE,
        COMPRESSION_LEVEL,
        ZIP_COMMENT,
        VERIFY,
        FAKE_DUPLICATE_ENTRIES,
        CORRUPT_CRCS,
        ANTI_EXTRACTION,
        ATTEMPT_COMPUTE_MAXS;

        public String getKeyString() {
            return name().toLowerCase();
        }
    }
}

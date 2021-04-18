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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import xyz.itzsomebody.radon.exclusions.ExclusionManager;
import xyz.itzsomebody.radon.transformers.Transformer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for the config
 *
 * @author itzsomebody
 */
public class ConfigurationParser extends ObjectMapper {
    private final InputStream configStream;

    public ConfigurationParser(InputStream configStream) {
        super();
        registerModule(new SimpleModule()
                .addDeserializer(Transformer.class, new TransformerDeserializer())
                .addDeserializer(ExclusionManager.class, new ExclusionsDeserializer())
        );
        this.configStream = configStream;
    }

    public ObfConfig parseConfig() throws IOException {
        return readValue(configStream, ObfConfig.class);
    }
}

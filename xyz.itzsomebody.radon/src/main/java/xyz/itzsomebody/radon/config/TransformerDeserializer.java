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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.exceptions.FatalRadonException;
import xyz.itzsomebody.radon.exceptions.PreventableRadonException;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Deseralizer for transformers
 *
 * @author itzsomebody
 */
public class TransformerDeserializer extends JsonDeserializer<Transformer> {
    private static Transformer transformerFor(String name) {
        try {
            var transformerEnum = Transformers.valueOf(name.toUpperCase());
            var clazz = transformerEnum.getTransformerClass();
            var cnstr = clazz.getConstructor();
            cnstr.setAccessible(true);
            return cnstr.newInstance();
        } catch (IllegalArgumentException e) {
            throw new PreventableRadonException("Transformer \"" + name + "\" is an unknown transformer");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new FatalRadonException(e);
        } catch (InvocationTargetException e) {
            throw new FatalRadonException(e.getTargetException());
        }
    }

    @Override
    public Transformer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        var entry = node.fields().next();
        var transformer = transformerFor(entry.getKey());
        if (entry.getValue().isBoolean()) {
            if (entry.getValue().asBoolean()) {
                return transformer;
            } else {
                return null; // fixme: don't caboose null
            }
        } else {
            var mapper = new ObjectMapper(new JsonFactory());
            mapper.registerModule(new SimpleModule().addDeserializer(Dictionary.class, new DictionaryDeserializer()));
            return mapper.readerForUpdating(transformer).readValue(entry.getValue());
        }
    }
}

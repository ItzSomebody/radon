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

package xyz.itzsomebody.radon.transformers;

import xyz.itzsomebody.radon.transformers.misc.*;
import xyz.itzsomebody.radon.transformers.strings.AESPCBCStringEncryption;
import xyz.itzsomebody.radon.transformers.strings.StaticFieldStrPool;
import xyz.itzsomebody.radon.transformers.strings.Str2Base64Encoding;

public enum Transformers {
    // Misc.
    ADD_BRIDGE_ACCESS_FLAG(AddBridgeAccess.class),
    ADD_DEPRECATED_ACCESS_FLAG(AddDeprecatedAccess.class),
    ADD_SYNTHETIC_ACCESS_FLAG(AddSyntheticAccess.class),
    ADD_TRASH_CLASSES(AddTrashClasses.class),
    INJECT_ANTI_DEBUGGER(AntiDebugger.class),
    INJECT_EXPIRATION_KILL_SWITCH(ExpirationKillSwitch.class),
    RENAMER(Renamer.class),
    RESOURCE_RENAMER(ResourceRenamer.class),
    SCRAMBLE_LINE_NUMBERS(ScrambleLineNumbers.class),
    SHUFFLE_MEMBERS(ShuffleMembers.class),
    WATERMARK(Watermarker.class),

    // String encryption/encoding
    STRING_TO_BASE64_ENCODING(Str2Base64Encoding.class),
    POOL_STRINGS_TO_STATIC_FIELD(StaticFieldStrPool.class),
    AES_PCBC_STRING_ENCRYPTION(AESPCBCStringEncryption.class),

    ;//TODO

    private final Class<? extends Transformer> transformerClass;

    Transformers(Class<? extends Transformer> transformerClass) {
        this.transformerClass = transformerClass;
    }

    public Class<? extends Transformer> getTransformerClass() {
        return transformerClass;
    }

    public String getConfigName() {
        return name().toLowerCase();
    }
}

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

package me.itzsomebody.radon.exclusions;

/**
 * All the valid exclusion types in an {@link Enum} representation.
 *
 * @author ItzSomebody
 */
public enum ExclusionType {
    GLOBAL,
    EXTENDS, // TODO
    IMPLEMENTS, // TODO
    OPTIMIZER,
    SHRINKER,
    RENAMER,
    REFERENCE_OBFUSCATION,
    NUMBER_OBFUSCATION,
    STRING_ENCRYPTION,
    FLOW_OBFUSCATION,
    HIDE_CODE,
    EXPIRATION,
    SHUFFLER,
    EJECTOR,
    INSTRUCTION_SET_REDUCER,
    BAD_ANNOTATIONS,
    STATIC_INITIALIZATION,
    RESOURCE_RENAMER,
    VIRTUALIZER,
    WATERMARKER,
    ANTI_TAMPER,
    TRASH_CLASSES,
    PACKER;

    public String getName() {
        return name().toLowerCase();
    }
}

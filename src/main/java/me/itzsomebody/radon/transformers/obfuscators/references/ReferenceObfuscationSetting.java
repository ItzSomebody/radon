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

package me.itzsomebody.radon.transformers.obfuscators.references;

public enum ReferenceObfuscationSetting {
    HIDE_WITH_INDY(new InvokedynamicTransformer()),
    HIDE_WITH_FAST_INDY(new FastInvokedynamicTransformer());

    private ReferenceObfuscation referenceObfuscation;

    ReferenceObfuscationSetting(ReferenceObfuscation referenceObfuscation) {
        this.referenceObfuscation = referenceObfuscation;
    }

    public ReferenceObfuscation getReferenceObfuscation() {
        return referenceObfuscation;
    }

    public String getName() {
        return name().toLowerCase();
    }
}

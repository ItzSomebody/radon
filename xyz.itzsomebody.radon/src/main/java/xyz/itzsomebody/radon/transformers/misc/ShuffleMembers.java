/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class ShuffleMembers extends Transformer {
    @JsonProperty("shuffle_methods")
    private boolean shuffleMethods;

    @JsonProperty("shuffle_fields")
    private boolean shuffleFields;

    @Override
    public void transform() {
        if (shuffleFields || shuffleMethods) {
            var count = new AtomicInteger();
            classes().stream().filter(this::notExcluded).forEach(cw -> {
                // FIXME: Also update list of method/field wrappers
                if (shuffleMethods) {
                    Collections.shuffle(cw.getClassNode().methods);
                    count.addAndGet(cw.getMethods().size());
                }
                if (shuffleFields) {
                    Collections.shuffle(cw.getClassNode().fields);
                    count.addAndGet(cw.getFields().size());
                }
            });
            RadonLogger.info("Shuffled " + count.get() + " members");
        }
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.SHUFFLE_MEMBERS;
    }

    @Override
    public String getConfigName() {
        return Transformers.SHUFFLE_MEMBERS.getConfigName();
    }
}

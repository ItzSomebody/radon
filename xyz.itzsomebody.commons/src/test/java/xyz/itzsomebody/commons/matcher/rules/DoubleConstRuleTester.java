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

package xyz.itzsomebody.commons.matcher.rules;

import org.junit.Assert;
import org.junit.Test;
import xyz.itzsomebody.commons.TestingUtils;
import xyz.itzsomebody.commons.matcher.InstructionPattern;

import java.util.concurrent.ThreadLocalRandom;

public class DoubleConstRuleTester {
    @Test
    public void testMatch() {
        var examples = new double[]{
                0D,
                1D,
                ThreadLocalRandom.current().nextDouble()
        };

        for (var example : examples) {
            var pattern = new InstructionPattern(new DoubleConstRule());
            var matcher = pattern.matcher(TestingUtils.loadDouble(example));
            Assert.assertTrue(matcher.matches());
            Assert.assertTrue(matcher.find());
        }
    }
}

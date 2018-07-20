/*
 * Copyright (C) 2018 ItzSomebody
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

package me.itzsomebody.radon.transformers.invokedynamic;

import me.itzsomebody.radon.methods.InvokeDynamicBSMGenerator;
import me.itzsomebody.radon.utils.BytecodeUtils;
import org.objectweb.asm.tree.*;

/**
 * Transformer that applies an InvokeDynamic which attempts to prevent
 * deobfuscation to happen if {@link Runtime#getRuntime()} is disabled.
 * Specifically java-deobfuscator's MethodExecutor.
 *
 * @author ItzSomebody
 * @author Licel (transformer based on IndyProtector)
 */
public class NormalInvokeDynamic extends LightInvokeDynamic {
    @Override
    protected void addBSM(ClassNode bsmHost, String methodName) {
        bsmHost.methods.add(InvokeDynamicBSMGenerator.normalBSM(methodName, bsmHost.name));
        bsmHost.access = BytecodeUtils.makePublic(bsmHost.access);
    }
}

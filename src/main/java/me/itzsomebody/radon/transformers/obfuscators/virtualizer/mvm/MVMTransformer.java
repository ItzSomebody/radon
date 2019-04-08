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

package me.itzsomebody.radon.transformers.obfuscators.virtualizer.mvm;

import java.util.stream.Stream;
import me.itzsomebody.radon.transformers.obfuscators.virtualizer.Virtualizer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Translates Java bytecode into Meme VM (MVM) instructions. The reason I chose Meme VM as a name is
 * because the protection is minimal as far as a VM is concerned (1-to-1 instruction conversion and
 * close to Java bytecode).
 *
 * @author ItzSomebody
 */
public class MVMTransformer extends Virtualizer {
    @Override
    public void transform() {
        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;

                    int leeway = getSizeLeeway(methodNode);
                    if (leeway <= 30000) // Virtualization of huge method = bad
                        return;

                    if (canProtect(methodNode.instructions)) {
                        Stream.of(methodNode.instructions.toArray()).forEach(insn -> {
                            // TODO
                        });
                    }
                }));
    }

    private static boolean canProtect(InsnList insnList) {
        return Stream.of(insnList.toArray()).noneMatch(insn -> insn.getOpcode() == INVOKEDYNAMIC
                || (insn instanceof LdcInsnNode && ((Type) ((LdcInsnNode) insn).cst).getSort() == Type.METHOD));
    }
}
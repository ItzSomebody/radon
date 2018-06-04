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

package me.itzsomebody.radon.transformers.misc;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

/**
 * Transformer that adds an expiration block of code to <init> methods.
 *
 * @author ItzSomebody
 * @author Allatori Dev Team (transformer based on Allatori's
 * expiration obfuscation)
 */
public class Expiry extends AbstractTransformer {
    /**
     * The expiry time as a {@link Long}.
     */
    private long expiryTime;

    /**
     * The expiry message to display when expiry time is exceeded.
     */
    private String expiryMsg;

    /**
     * Constructor used to create an {@link Expiry} object.
     *
     * @param expiryTime expiration time as a {@link Long}.
     * @param expiryMsg  expiration message as a {@link String}.
     */
    public Expiry(long expiryTime, String expiryMsg) {
        this.expiryTime = expiryTime;
        this.expiryMsg = expiryMsg;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started expiry transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Expiry")).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "Expiry")
                            && methodNode.name.equals("<init>") && methodSize(methodNode) < 60000).forEach(methodNode -> {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        BytecodeUtils.returnExpiry(this.expiryTime, this.expiryMsg));
                counter.incrementAndGet();
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " expiration code blocks."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}

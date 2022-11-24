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
import org.objectweb.asm.tree.InsnList;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.radon.exceptions.PreventableRadonException;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.RandomUtils;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

/**
 * Inserts an expiration block of instructions in each constructor method.
 *
 * @author itzsomebody
 */
/*
 * Another really easy thing to patch out. So I also turned this into a production test of codegen.
 */
public class ExpirationKillSwitch extends Transformer {
    private static final Class<?>[] EXCEPTIONS = {
            Throwable.class,
            Exception.class,
            RuntimeException.class,
            NullPointerException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    };

    @JsonProperty("message")
    private String message = "tucks";

    private long expirationTime;

    @JsonProperty("inject_joption_pane")
    private boolean injectJOptionPane;

    @JsonProperty("expiration_time")
    public void setExpirationTime(String date) {
        try {
            expirationTime = new SimpleDateFormat("MM/dd/yyyy").parse(date).getTime();
        } catch (ParseException e) {
            throw new PreventableRadonException("Error parsing " + date + " into format MM/dd/yyyy: (" + e.getMessage() + ", offset=" + e.getErrorOffset() + ")");
        }
    }

    @Override
    public void transform() {
        var counter = new AtomicInteger();
        classes().stream().filter(this::notExcluded).forEach(cw -> {
            cw.methodStream().filter(mw -> notExcluded(mw) && mw.getMethodNode().name.startsWith("<")).forEach(mw -> {
                mw.getMethodNode().instructions.insert(generateExpirationCode());
                counter.incrementAndGet();
            });
        });
        RadonLogger.info("Added " + counter.get() + " expiration code blocks");
    }

    // For reference
    private static void example() {
        // replace 0L with time and "tucks" with message
        if (new Date().after(new Date(0L))) {
            JOptionPane.showMessageDialog(null, "tucks");
            throw new RuntimeException("tucks");
        }
    }

    private InsnList generateExpirationCode() {
        var condition = new BytecodeBlock()
                .append(
                        newInstance(
                                WrappedType.from(Date.class),
                                Collections.emptyList(),
                                Collections.emptyList()
                        ).invoke(
                                "after",
                                List.of(WrappedType.from(Date.class)),
                                WrappedType.from(boolean.class),
                                newInstance(
                                        WrappedType.from(Date.class),
                                        List.of(WrappedType.from(long.class)),
                                        List.of(longConst(expirationTime))
                                )
                        ).getInstructions()
                );

        var trueBlock = new BytecodeBlock();
        if (injectJOptionPane) {
            trueBlock.append(invokeStatic(
                    WrappedType.from(JOptionPane.class),
                    "showMessageDialog",
                    List.of(nullConst(Component.class), stringConst(message)),
                    List.of(WrappedType.from(Component.class), WrappedType.from(Object.class)),
                    WrappedType.from(void.class)
            ).getInstructions());
        }
        trueBlock.append(newInstance(
                WrappedType.from(EXCEPTIONS[RandomUtils.randomInt(EXCEPTIONS.length)]),
                List.of(WrappedType.from(String.class)),
                List.of(stringConst(message))
        ).throwMe().getInstructions());

        return ifBlock(condition, trueBlock, new BytecodeBlock()).getInstructions().compile();
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.INJECT_EXPIRATION_KILL_SWITCH;
    }

    @Override
    public String getConfigName() {
        return Transformers.INJECT_EXPIRATION_KILL_SWITCH.getConfigName();
    }
}

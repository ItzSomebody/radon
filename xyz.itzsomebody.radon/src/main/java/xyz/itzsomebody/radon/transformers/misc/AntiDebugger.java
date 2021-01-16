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

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.radon.config.Configuration;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.RandomUtils;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

/**
 * Blocks debugging options on the commandline.
 *
 * @author vovanre
 * @author itzsomebody
 */
/*
 * Note from itzsomebody: this is *really, really* easy to remove and/or bypass; however, I don't really want to
 * vest much time in anti-debugging. So as a result, I turned this into a production test of the codegen. :PP
 */
public class AntiDebugger extends Transformer {
    private static final String[] DEBUG_OPTIONS = new String[]{"-agentlib:jdwp", "-Xdebug", "-Xrunjdwp:", "-javaagent:"};
    private String message;
    private AtomicInteger debugOptionIndex;

    @Override
    public void transform() {
        var counter = new AtomicInteger();
        debugOptionIndex = new AtomicInteger();

        classes().stream().filter(cw -> !cw.isInterface() && notExcluded(cw)).forEach(cw -> {
            var clinit = cw.getMethodNode("<clinit>", "()V");
            if (clinit == null) {
                clinit = new MethodNode(ACC_PRIVATE | ACC_STATIC, "<clinit>", "()V", null, null);
                clinit.visitInsn(RETURN);
                cw.addMethod(clinit);
            }

            var checkCount = RandomUtils.randomInt(1, DEBUG_OPTIONS.length);
            for (int i = 0; i < checkCount; i++) {
                clinit.instructions.insert(generateCheck());
                counter.incrementAndGet();
            }
        });

        RadonLogger.info("Injected " + counter.get() + " anti-debugger checks");
    }

    private InsnList generateCheck() {
        var trueBlock = new BytecodeBlock();
        if (RandomUtils.randomBoolean()) {
            trueBlock.append(getStatic(
                    WrappedType.from(System.class),
                    "out",
                    WrappedType.from(PrintStream.class)
            ).invoke(
                    "println",
                    List.of(WrappedType.from(String.class)),
                    WrappedType.from(void.class),
                    stringConst(message)
            ).getInstructions());
            if (RandomUtils.randomBoolean()) {
                trueBlock.append(invokeStatic(
                        WrappedType.from(System.class),
                        "exit",
                        List.of(intConst(RandomUtils.randomInt())),
                        List.of(WrappedType.from(int.class)),
                        WrappedType.from(void.class)
                ).getInstructions());
            } else {
                trueBlock.append(invokeStatic(
                        WrappedType.from(Runtime.class),
                        "getRuntime",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        WrappedType.from(Runtime.class)
                ).invoke(
                        "halt",
                        List.of(WrappedType.from(int.class)),
                        WrappedType.from(void.class),
                        intConst(RandomUtils.randomInt())
                ).getInstructions());
            }
        } else {
            trueBlock.append(newInstance(
                    WrappedType.from(RuntimeException.class),
                    List.of(WrappedType.from(String.class)),
                    List.of(stringConst(message))
            ).throwMe().getInstructions());
        }

        return ifBlock(
                isDebugExpression().getInstructions(),
                trueBlock,
                new BytecodeBlock()
        ).getInstructions().compile();
    }

    private IRExpression isDebugExpression() {
        var isUpper = RandomUtils.randomBoolean();
        var argument = DEBUG_OPTIONS[debugOptionIndex.incrementAndGet() % DEBUG_OPTIONS.length];
        argument = isUpper ? argument.toUpperCase() : argument.toLowerCase();

        var getArguments = invokeVirtual(
                invokeVirtual(
                        invokeVirtual(
                                invokeStatic(WrappedType.from(ManagementFactory.class), "getRuntimeMXBean", Collections.emptyList(), Collections.emptyList(), WrappedType.from(RuntimeMXBean.class)),
                                WrappedType.from(RuntimeMXBean.class),
                                "getInputArguments",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                WrappedType.from(List.class)
                        ),
                        WrappedType.from(Object.class),
                        "toString",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        WrappedType.from(String.class)
                ),
                WrappedType.from(String.class),
                isUpper ? "toUpperCase" : "toLowerCase",
                Collections.emptyList(),
                Collections.emptyList(),
                WrappedType.from(String.class)
        );
        return invokeVirtual(
                getArguments,
                WrappedType.from(String.class),
                "contains",
                List.of(stringConst(argument)),
                List.of(WrappedType.from(CharSequence.class)),
                WrappedType.from(boolean.class)
        );
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.INJECT_ANTI_DEBUGGER;
    }

    @Override
    public void loadSetup(Configuration config) {
        message = config.getOrDefault(getLocalConfigPath() + ".message", "java.lang.NullPointerException"); // lul
    }

    @Override
    public String getConfigName() {
        return Transformers.INJECT_ANTI_DEBUGGER.getConfigName();
    }
}

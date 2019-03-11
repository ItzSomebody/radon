package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;

public class VisibleParameterAnnotationsRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && methodWrapper.methodNode.visibleParameterAnnotations != null).forEach(methodWrapper -> {

                    counter.addAndGet(methodWrapper.methodNode.visibleAnnotableParameterCount);
                    methodWrapper.methodNode.visibleParameterAnnotations = null;
                }));

        Logger.stdOut(String.format("Removed %d visible parameter annotations.", counter.get()));
    }

    @Override
    public String getName() {
        return "Visible Parameter Annotations Remover";
    }
}

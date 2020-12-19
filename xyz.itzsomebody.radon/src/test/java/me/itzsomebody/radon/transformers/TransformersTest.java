package me.itzsomebody.radon.transformers;

import org.junit.Assert;
import org.junit.Test;
import xyz.itzsomebody.radon.transformers.Transformers;

public class TransformersTest {
    @Test
    public void ensureTransformerExclusionTypeNonNull() throws Exception {
        for (var transformerEnum : Transformers.values()) {
            var transformerConstructor = transformerEnum.getTransformerClass().getConstructor();
            transformerConstructor.setAccessible(true);
            var transformerInstance = transformerConstructor.newInstance();

            // Often I'm dumb and forget to put an exclusion type
            Assert.assertNotNull(transformerInstance.getExclusionType());
        }
    }
}

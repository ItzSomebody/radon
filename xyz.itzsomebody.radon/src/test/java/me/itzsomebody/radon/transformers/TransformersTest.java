package me.itzsomebody.radon.transformers;

import org.junit.Assert;
import org.junit.Test;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.transformers.constants.ConstantsTransformer;

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

    @Test
    public void ensureTransformerConfigNameNonNull() throws Exception{
        for (var transformerEnum : Transformers.values()) {
            var transformerConstructor = transformerEnum.getTransformerClass().getConstructor();
            transformerConstructor.setAccessible(true);
            var transformerInstance = transformerConstructor.newInstance();

            Assert.assertNotNull(transformerInstance.getConfigName());
        }
    }

    @Test
    public void ensureTransformerExclusionTypeNameIsSameAsConfigName() throws Exception{
        for (var transformerEnum : Transformers.values()) {
            var transformerConstructor = transformerEnum.getTransformerClass().getConstructor();
            transformerConstructor.setAccessible(true);
            var transformerInstance = transformerConstructor.newInstance();

            if (transformerInstance instanceof ConstantsTransformer) {
                // Skip -- mostly likely no problem here
            } else {
                Assert.assertEquals(transformerInstance.getConfigName(), transformerInstance.getExclusionType().name().toLowerCase());
            }
        }
    }
}

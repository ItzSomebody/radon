package me.itzsomebody.radon.dictionaries;

import me.itzsomebody.radon.utils.RandomUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyInt;

@RunWith(PowerMockRunner.class)
public class CustomDictionaryTest {

    @PrepareForTest({RandomUtils.class})
    @Test
    public void testRandomString() {
        PowerMockito.mockStatic(RandomUtils.class);
        PowerMockito.when(RandomUtils.getRandomInt(Math.abs(anyInt())))
                .thenReturn(4);
        Assert.assertEquals("aaa",
                new CustomDictionary("foobar").randomString(3));
    }

    @PrepareForTest({RandomUtils.class})
    @Test
    public void testUniqueRandomString() {
        PowerMockito.mockStatic(RandomUtils.class);
        PowerMockito.when(RandomUtils.getRandomInt(Math.abs(anyInt())))
                .thenReturn(4);

        Assert.assertEquals("",
                new CustomDictionary("foobar").uniqueRandomString(-1));
        Assert.assertEquals("aaa",
                new CustomDictionary("foobar").uniqueRandomString(3));
    }

    @Test
    public void testNextUniqueString() {
        Assert.assertEquals("f",
                new CustomDictionary("foobar").nextUniqueString());
    }

    @Test
    public void testLastUniqueString() {
        Assert.assertNull(new CustomDictionary("foobar").lastUniqueString());
    }

    @Test
    public void testGetDictionaryName() {
        Assert.assertEquals("foobar",
                new CustomDictionary("foobar").getDictionaryName());
    }

    @Test
    public void testCopy() {
        Assert.assertEquals("foobar",
                new CustomDictionary("foobar").copy().getDictionaryName());
    }
}

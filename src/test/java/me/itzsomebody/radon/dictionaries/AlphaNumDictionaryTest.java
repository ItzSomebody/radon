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
public class AlphaNumDictionaryTest {

    @PrepareForTest({RandomUtils.class})
    @Test
    public void testRandomString() {
        PowerMockito.mockStatic(RandomUtils.class);
        PowerMockito.when(RandomUtils.getRandomInt(Math.abs(anyInt())))
                .thenReturn(4);
        Assert.assertEquals("EEE", new AlphaNumDictionary().randomString(3));
    }

    @PrepareForTest({RandomUtils.class})
    @Test
    public void testUniqueRandomString() {
        PowerMockito.mockStatic(RandomUtils.class);
        PowerMockito.when(RandomUtils.getRandomInt(Math.abs(anyInt())))
                .thenReturn(4);

        Assert.assertEquals("",
                new AlphaNumDictionary().uniqueRandomString(-1));
        Assert.assertEquals("EEE",
                new AlphaNumDictionary().uniqueRandomString(3));
    }

    @Test
    public void testNextUniqueString() {
        Assert.assertEquals("A", new AlphaNumDictionary().nextUniqueString());
    }

    @Test
    public void testLastUniqueString() {
        Assert.assertNull(new AlphaNumDictionary().lastUniqueString());
    }

    @Test
    public void testGetDictionaryName() {
        Assert.assertEquals("alphanumeric",
                new AlphaNumDictionary().getDictionaryName());
    }

    @Test
    public void testCopy() {
        Assert.assertEquals("alphanumeric",
                new AlphaNumDictionary().copy().getDictionaryName());
    }
}

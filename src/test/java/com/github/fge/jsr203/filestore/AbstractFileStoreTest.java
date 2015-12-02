package com.github.fge.jsr203.filestore;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.Random;
import java.util.function.IntPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

public final class AbstractFileStoreTest
{
    @SuppressWarnings("CodeBlock2Expr")
    private static final IntPredicate VALID_CODEPOINT = value -> {
        return Character.isValidCodePoint(value)
            && Character.UnicodeBlock.of(value) != null;
    };

    private static final long ATTR_SIZE = 10L;

    private String attrName;

    @BeforeMethod
    public void initAttrName()
    {
        @SuppressWarnings("UnsecureRandomNumberGeneration")
        final Random random = new Random(System.currentTimeMillis());

        final StringBuilder sb = new StringBuilder();

        random.ints().filter(VALID_CODEPOINT).limit(ATTR_SIZE)
            .forEach(sb::appendCodePoint);

        attrName = sb.toString();
    }

    @Test
    public void defaultsTest()
        throws IOException
    {
        final FileStore fileStore = new TestFileStore();

        assertThat(fileStore.getTotalSpace()).isEqualTo(Long.MAX_VALUE);
        assertThat(fileStore.getUnallocatedSpace()).isEqualTo(Long.MAX_VALUE);
        assertThat(fileStore.getUsableSpace()).isEqualTo(Long.MAX_VALUE);
        assertThat(fileStore.getFileStoreAttributeView(
            FileStoreAttributeView.class)).isNull();

        try {
            fileStore.getAttribute(attrName);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException ignored) {
        }
    }
}

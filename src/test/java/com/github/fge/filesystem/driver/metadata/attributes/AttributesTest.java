/*
 * Copyright (c) 2015, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.filesystem.driver.metadata.attributes;

import com.github.fge.filesystem.driver.metadata.PathMetadata;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AttributesTest<A extends BasicAttributes<Object>>
{
    protected final Map<String, Object> values = new HashMap<>();

    protected PathMetadata<Object> pathMetadata;
    protected A attributes;

    @BeforeMethod
    public abstract void init();

    @Test
    public final void readAllTest()
    {
        final Map<String, Object> all = attributes.getAllAttributes();

        assertThat(all).isEqualTo(values);
    }
}

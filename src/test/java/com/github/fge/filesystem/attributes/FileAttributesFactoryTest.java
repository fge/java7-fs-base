/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.attributes;

import com.github.fge.filesystem.attributes.testclasses.ArgType1;
import com.github.fge.filesystem.attributes.testclasses.ProtectedAcl;
import com.github.fge.filesystem.attributes.testclasses.PublicAcl;
import com.github.fge.filesystem.attributes.testclasses.PublicAclNonPublicConstructor;
import com.github.fge.filesystem.exceptions.InvalidAttributeProviderException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

public final class FileAttributesFactoryTest
{
    @Test
    public void registeringNewProviderWorksWhenClassIsCorrect()
    {
        new FileAttributesFactory()
        {
            {
                addImplementation("acl", PublicAcl.class, ArgType1.class);
            }
        };

        assertTrue(true);
    }

    @Test
    public void registeringAbstractProviderFails()
    {
        try {
            new FileAttributesFactory()
            {
                {
                    addImplementation("acl", ProtectedAcl.class,ArgType1.class);
                }
            };
            failBecauseExceptionWasNotThrown(
                InvalidAttributeProviderException.class
            );
        } catch (InvalidAttributeProviderException e) {
            assertThat(e).hasMessage("provider class must not be abstract");
        }
    }

    @Test
    public void registeringPackageLocalProviderFails()
    {
        try {
            new FileAttributesFactory()
            {
                {
                    addImplementation("acl", PackageLocalAcl.class,
                        ArgType1.class);
                }
            };
            failBecauseExceptionWasNotThrown(
                InvalidAttributeProviderException.class
            );
        } catch (InvalidAttributeProviderException e) {
            assertThat(e).hasMessage("provider class must be public");
        }
    }

    @Test
    public void registeringPublicClassWithNonPublicConstructorFails()
    {
        try {
            new FileAttributesFactory()
            {
                {
                    addImplementation("acl",
                        PublicAclNonPublicConstructor.class, ArgType1.class);
                }
            };
            failBecauseExceptionWasNotThrown(
                InvalidAttributeProviderException.class
            );
        } catch (InvalidAttributeProviderException e) {
            assertThat(e)
                .hasMessageStartingWith("no constructor found for class ");
        }
    }

    @Test
    public void registeringConstructorWithBadArgumentTypesFails()
    {
        try {
            new FileAttributesFactory()
            {
                {
                    addImplementation("acl", PublicAcl.class, Object.class);
                }
            };
            failBecauseExceptionWasNotThrown(
                InvalidAttributeProviderException.class
            );
        } catch (InvalidAttributeProviderException e) {
            assertThat(e)
                .hasMessageStartingWith("no constructor found for class ");
        }
    }

    @Test(dependsOnMethods = "registeringNewProviderWorksWhenClassIsCorrect")
    public void canGenerateProviderInstanceOfExactAttributeClassName()
        throws IOException
    {
        final FileAttributesFactory factory
            = new FileAttributesFactory()
        {
            {
                addImplementation("acl", PublicAcl.class, ArgType1.class);
            }
        };

        final AclFileAttributeView view
            = factory.getFileAttributeView(AclFileAttributeView.class,
                mock(ArgType1.class));

        assertThat(view).isNotNull()
            .isExactlyInstanceOf(PublicAcl.class);
    }
}
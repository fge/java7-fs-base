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
import com.github.fge.filesystem.attributes.testclasses.DummyPosix;
import com.github.fge.filesystem.attributes.testclasses.ProtectedAcl;
import com.github.fge.filesystem.attributes.testclasses.PublicAcl;
import com.github.fge.filesystem.attributes.testclasses.PublicAclNonPublicConstructor;
import com.github.fge.filesystem.exceptions.InvalidAttributeProviderException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

public final class FileAttributesFactoryTest
{
    @Test
    public void cannotRegisterProviderWithoutMetadataClass()
    {
        try {
            new FileAttributesFactory()
            {
                {
                    addImplementation("acl", PublicAcl.class);
                }
            };
            failBecauseExceptionWasNotThrown(
                IllegalArgumentException.class
            );
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("metadata class has not been set");
        }
    }


    @Test
    public void registeringNewProviderWorksWhenClassIsCorrect()
    {
        new FileAttributesFactory()
        {
            {
                setMetadataClass(ArgType1.class);
                addImplementation("acl", PublicAcl.class);
            }
        };

        assertThat(true).isTrue();
    }

    @Test
    public void registeringAbstractProviderFails()
    {
        try {
            new FileAttributesFactory()
            {
                {
                    setMetadataClass(ArgType1.class);
                    addImplementation("acl", ProtectedAcl.class);
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
                    setMetadataClass(ArgType1.class);
                    addImplementation("acl", PackageLocalAcl.class);
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
                    setMetadataClass(ArgType1.class);
                    addImplementation("acl",
                        PublicAclNonPublicConstructor.class);
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
                    setMetadataClass(Object.class);
                    addImplementation("acl", PublicAcl.class);
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
                setMetadataClass(ArgType1.class);
                addImplementation("acl", PublicAcl.class);
            }
        };

        final AclFileAttributeView view
            = factory.getFileAttributeView(AclFileAttributeView.class,
                mock(ArgType1.class));

        assertThat(view).isNotNull()
            .isExactlyInstanceOf(PublicAcl.class);
    }

    @Test(dependsOnMethods = "registeringNewProviderWorksWhenClassIsCorrect")
    public void canReturnSubclassOfRequiredAttributeViewClass()
        throws IOException
    {
        final FileAttributesFactory factory
            = new FileAttributesFactory()
        {
            {
                setMetadataClass(ArgType1.class);
                addImplementation("acl", PublicAcl.class);
            }
        };

        final FileOwnerAttributeView view
            = factory.getFileAttributeView(FileOwnerAttributeView.class,
                mock(ArgType1.class));

        assertThat(view).isNotNull()
            .isExactlyInstanceOf(PublicAcl.class);
    }

    @Test
    public void attributeProviderExtendingBasicReportsBasicViewSupported()
    {
        final FileAttributesFactory factory
            = new FileAttributesFactory()
        {
            {
                setMetadataClass(ArgType1.class);
                addImplementation("posix", DummyPosix.class);
            }
        };

        assertThat(factory.supportsFileAttributeView("basic"))
            .as("attribute provider extending basic supports basic")
            .isTrue();
    }
}

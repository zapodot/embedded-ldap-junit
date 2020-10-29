package org.zapodot.junit.ldap.junit5;

import org.zapodot.junit.ldap.internal.AbstractEmbeddedLdapBuilder;
import org.zapodot.junit.ldap.internal.EmbeddedLdapExtensionImpl;

import java.util.Objects;

/**
 * A builder providing a fluent way of defining {@link EmbeddedLdapExtension} instances.
 */
public class EmbeddedLdapExtensionBuilder extends AbstractEmbeddedLdapBuilder<EmbeddedLdapExtensionBuilder> {

    /**
     * Creates a new builder
     *
     * @return a new EmbeddedLdapExtensionBuilder instance
     */
    public static EmbeddedLdapExtensionBuilder newInstance() {
        return new EmbeddedLdapExtensionBuilder();
    }

    /**
     * Creates a new extension based on the information that was previously provided
     *
     * @return a new EmbeddedLdapExtension instance
     */
    public EmbeddedLdapExtension build() {
        Objects.requireNonNull(bindDSN, "\"bindDSN\" can not be null");
        return EmbeddedLdapExtensionImpl.createForConfiguration(createInMemoryServerConfiguration(),
                authenticationConfiguration,
                ldifsToImport);
    }

    @Override
    protected EmbeddedLdapExtensionBuilder getThis() {
        return this;
    }
}

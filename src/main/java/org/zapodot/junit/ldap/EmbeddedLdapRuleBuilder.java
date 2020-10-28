package org.zapodot.junit.ldap;

import org.zapodot.junit.ldap.internal.AbstractEmbeddedLdapBuilder;
import org.zapodot.junit.ldap.internal.EmbeddedLdapRuleImpl;

import java.util.Objects;

/**
 * A builder providing a fluent way of defining EmbeddedLdapRule instances
 */
public class EmbeddedLdapRuleBuilder extends AbstractEmbeddedLdapBuilder<EmbeddedLdapRuleBuilder> {

    /**
     * Creates a new builder
     *
     * @return a new EmbeddedLdapRuleBuilder instance
     */
    public static EmbeddedLdapRuleBuilder newInstance() {
        return new EmbeddedLdapRuleBuilder();
    }

    @Override
    protected EmbeddedLdapRuleBuilder getThis() {
        return this;
    }

    /**
     * Creates a new rule based on the information that was previously provided
     *
     * @return a new EmbeddedLdapRule instance
     */
    public EmbeddedLdapRule build() {
        Objects.requireNonNull(bindDSN, "\"bindDSN\" can not be null");
        return EmbeddedLdapRuleImpl.createForConfiguration(createInMemoryServerConfiguration(),
                                                           authenticationConfiguration,
                                                           ldifsToImport);
    }

}

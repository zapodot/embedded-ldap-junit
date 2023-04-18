package org.zapodot.ldap.jupiter;

import org.zapodot.junit.ldap.AbstractEmbeddedLdapSupportBuilder;

public class EmbeddedLdapExtensionBuilder extends AbstractEmbeddedLdapSupportBuilder<EmbeddedLdapExtension> {

    public static EmbeddedLdapExtensionBuilder newInstance() {
        return new EmbeddedLdapExtensionBuilder();
    }

    @Override
    public EmbeddedLdapExtension build() {
        return null;
    }
}

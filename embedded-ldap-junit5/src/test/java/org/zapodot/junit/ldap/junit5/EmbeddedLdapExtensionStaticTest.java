package org.zapodot.junit.ldap.junit5;

import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmbeddedLdapExtensionStaticTest {
    public static final String DOMAIN_DSN = "dc=zapodot,dc=org";

    @RegisterExtension
    public static EmbeddedLdapExtension embeddedLdapExtension = EmbeddedLdapExtensionBuilder
            .newInstance()
            .usingDomainDsn(DOMAIN_DSN)
            .usingBindDSN("cn=Directory manager")
            .usingBindCredentials("testPass")
            .importingLdifs("example.ldif")
            .build();

    @Test
    void testCheck() throws Exception {
        final LDAPInterface ldapConnection = embeddedLdapExtension.ldapConnection();
        assertEquals(4, ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=*)").getEntryCount());

    }
}
